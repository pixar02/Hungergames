package me.minebuilders.hg.mobhandler;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.minebuilders.hg.Game;
import me.minebuilders.hg.HG;

public class Spawner implements Runnable{

	private Game g;
	private int id;
	private Random rg = new Random();

	public Spawner(Game game, int i) {
		this.g = game;
		this.id = Bukkit.getScheduler().scheduleSyncRepeatingTask(HG.plugin, this, i, i);
	}

	@Override
	public void run() {
		for (String s : g.getPlayers()) {
			Player p = Bukkit.getPlayer(s);
			if (p != null) {
				Location l = p.getLocation();
				World w = l.getWorld();
				int x = l.getBlockX();
				int z = l.getBlockZ();
				int y = l.getBlockY();

				int ran1 = getRandomNumber();
				int ran2 = getRandomNumber();

				x = x + ran1;
				z = z + ran2;

				l = getSafeLoc(w, x, y, z, 30);

				if (g.isInRegion(l))
					w.spawnEntity(l, pickRandomMob(!isDay(w), rg.nextInt(10)));
			}
		}
	}

	public boolean isDay(World w) {
		long time = w.getTime();
		return time < 12300 || time > 23850;
	}

	private EntityType pickRandomMob(boolean isNight, int x) {
		if (isNight) {
			if (x < 5) 
				return EntityType.ZOMBIE;
			if (x < 8) 
				return EntityType.SKELETON;

			return EntityType.CREEPER;
		} else {
			if (x < 3)
				return EntityType.COW;
			if (x < 6)
				return EntityType.PIG;
			if (x < 7) 
				return EntityType.CHICKEN;
			if (x < 8)
				return EntityType.SHEEP;
			if (x < 9)
				return EntityType.SPIDER;

			return EntityType.CREEPER;
		}
	}

	private int getRandomNumber() {
		int r = rg.nextInt(25) - rg.nextInt(25);
		if (r <= 6 && r >= -6) {
			return getRandomNumber();
		}
		return r;
	}

	private Location getSafeLoc(World w, int x, int y, int z, int trys) {
		if (trys <= 0) return null;
		
		Block b = w.getBlockAt(x,y,z);
		if (b.getType().isSolid()) {
			return getSafeLoc(w, x, y + 1, z, trys--);
		} else if (b.getRelative(BlockFace.DOWN).getType().equals(Material.AIR)) {
			return getSafeLoc(w, x, y - 1, z, trys--);
		} else if (b.getRelative(BlockFace.UP).getType().isSolid()) {
			int ran1 = getRandomNumber();
			int ran2 = getRandomNumber();

			x = x + ran1;
			z = z + ran2;
			return getSafeLoc(w, x, y, z, trys--);
		} else {
			return new Location(w, x,y,z);
		}
	}
	
	public void stop() {
		Bukkit.getScheduler().cancelTask(id);
	}
}
