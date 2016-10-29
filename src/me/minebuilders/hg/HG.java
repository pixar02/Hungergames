package me.minebuilders.hg;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import me.minebuilders.hg.commands.AddSpawnCmd;
import me.minebuilders.hg.commands.BaseCmd;
import me.minebuilders.hg.commands.CreateCmd;
import me.minebuilders.hg.commands.DebugCmd;
import me.minebuilders.hg.commands.DeleteCmd;
import me.minebuilders.hg.commands.JoinCmd;
import me.minebuilders.hg.commands.KitCmd;
import me.minebuilders.hg.commands.LeaveCmd;
import me.minebuilders.hg.commands.ListCmd;
import me.minebuilders.hg.commands.ListGamesCmd;
import me.minebuilders.hg.commands.ReloadCmd;
import me.minebuilders.hg.commands.SetExitCmd;
import me.minebuilders.hg.commands.SetLobbyWallCmd;
import me.minebuilders.hg.commands.StartCmd;
import me.minebuilders.hg.commands.StopCmd;
import me.minebuilders.hg.commands.TeamCmd;
import me.minebuilders.hg.commands.ToggleCmd;
import me.minebuilders.hg.commands.WandCmd;
import me.minebuilders.hg.data.Data;
import me.minebuilders.hg.data.RandomItems;
import me.minebuilders.hg.listeners.CancelListener;
import me.minebuilders.hg.listeners.CommandListener;
import me.minebuilders.hg.listeners.GameListener;
import me.minebuilders.hg.listeners.WandListener;
import me.minebuilders.hg.managers.ItemStackManager;
import me.minebuilders.hg.managers.KillManager;
import me.minebuilders.hg.managers.KitManager;
import me.minebuilders.hg.managers.Manager;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

public class HG extends JavaPlugin {
	
	/** Things to fix
	 * 
	 * Block breaking on selection of left click
	 * Colors are fucking ugly
	 * Cant select kits when in game
	 */

	//Maps
	public HashMap<String, BaseCmd> cmds = new HashMap<String, BaseCmd>();
	public HashMap<UUID, PlayerData> players = new HashMap<UUID, PlayerData>();
	public HashMap<UUID, PlayerSession> playerses = new HashMap<UUID, PlayerSession>();
	public HashMap<Integer, ItemStack> items = new HashMap<Integer, ItemStack>();
	
	//Lists
	public List<Game> games = new ArrayList<Game>();
	
	//Instances
	public static HG plugin;
	public static Manager manager;
	public static Data arenaconfig;
	public static KillManager killmanager;
	public static RandomItems ri;
	public KitManager kit;
	public ItemStackManager ism;
	
	@Override
	public void onEnable() {
		new Config(this);
		plugin = this;
		arenaconfig = new Data(this);
		killmanager = new KillManager();
		kit = new KitManager();
		ism = new ItemStackManager(this);
		ri = new RandomItems(this);
		manager = new Manager(this);
		getCommand("hg").setExecutor(new CommandListener(this));
		if(Config.leaveCmd == true){
			getCommand("leave").setExecutor(new Leave());
		}
		getServer().getPluginManager().registerEvents(new WandListener(this), this);
		getServer().getPluginManager().registerEvents(new CancelListener(this), this);
		getServer().getPluginManager().registerEvents(new GameListener(this), this);
		loadCmds();
		Util.log("Hugergames has been enabled!");
	}
	
	@Override
	public void onDisable() {
		stopAll();
		plugin = null;
		manager = null;
		arenaconfig = null;
		killmanager = null;
		kit = null;
		ism = null;
		ri = null;
		Util.log("Hugergames has been disabled!");
	}

	public void loadCmds() {
		cmds.put("team", new TeamCmd());
		cmds.put("addspawn", new AddSpawnCmd());
		cmds.put("create", new CreateCmd());
		cmds.put("join", new JoinCmd());
		cmds.put("leave", new LeaveCmd());
		cmds.put("reload", new ReloadCmd());
		cmds.put("setlobbywall", new SetLobbyWallCmd());
		cmds.put("wand", new WandCmd());
		cmds.put("kit", new KitCmd());
		cmds.put("debug", new DebugCmd());
		cmds.put("list", new ListCmd());
		cmds.put("listgames", new ListGamesCmd());
		cmds.put("forcestart", new StartCmd());
		cmds.put("stop", new StopCmd());
		cmds.put("toggle", new ToggleCmd());
		cmds.put("setexit", new SetExitCmd());
		cmds.put("delete", new DeleteCmd());


		for (String bc : cmds.keySet())
			getServer().getPluginManager().addPermission(new Permission("hg." + bc));
	}

	public void stopAll() {
		ArrayList<UUID> ps = new ArrayList<UUID>();
		for (Game g : games) {
			g.cancelTasks();
			g.forceRollback();
			ps.addAll(g.getPlayers());
		}
		for (UUID u : ps) {
			Player p = Bukkit.getPlayer(u);
			if (p != null) {
				players.get(u).getGame().leave(p);
			}
		}
		players.clear();
		games.clear();
	}
}
