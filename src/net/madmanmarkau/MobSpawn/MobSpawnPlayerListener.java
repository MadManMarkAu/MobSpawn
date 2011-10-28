package net.madmanmarkau.MobSpawn;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;

public class MobSpawnPlayerListener extends PlayerListener {
	public static MobSpawn plugin;
	
	public MobSpawnPlayerListener(MobSpawn instance) {
		plugin = instance;
	}

	public void onPlayerInteract(PlayerInteractEvent event)
	{
		if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
			Player player = event.getPlayer();
			MobSpawnParams params = plugin.getUserParams(player);
			
			if (params.isEnabled()
					&& player.isOnline()
					&& player.getItemInHand().getTypeId() == Material.BOW.getId()
					&& MobSpawnPermissions.has(player, "mobspawn.spawn")) {
	
				int[] ignoreList = {0, 8, 9, 10, 11, 51};
				
				BlockRayCaster rayCast = new BlockRayCaster(player, player.getLocation());
				
				if (rayCast.runCastWithIgnore(player.getWorld(), ignoreList)) {
					Block curBlock = rayCast.getCurrentBlock(player.getWorld());
	
					Location loc = new Location(player.getWorld(), curBlock.getX(), curBlock.getY() + 1, curBlock.getZ(), 0, 0);
					
					player.getWorld().spawnCreature(loc, params.getSelectedMob());
				}

				event.setCancelled(true);
			}
		}
	}
}
