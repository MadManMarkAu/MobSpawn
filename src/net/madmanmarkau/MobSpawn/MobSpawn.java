package net.madmanmarkau.MobSpawn;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;

public class MobSpawn extends JavaPlugin {
	public YamlConfiguration Config;
    public PluginDescriptionFile pdfFile;
	
    private MobSpawnPlayerListener playerListener = new MobSpawnPlayerListener(this);

    
    // Store which users have Silence enacted.
    private final HashMap<Player, MobSpawnParams> playerState = new HashMap<Player, MobSpawnParams>();

	@Override
	public void onDisable() {
		Messaging.logInfo("MobSpawn " + pdfFile.getVersion() + " unloaded", this);
	}

	@Override
	public void onEnable() {
		this.pdfFile = this.getDescription();

		setupPermissions();
		registerEvents();
		
		Messaging.logInfo("MobSpawn " + pdfFile.getVersion() + " loaded", this);
	}

	public void setupPermissions() {
		MobSpawnPermissions.initialize(this);
	}

	private void registerEvents() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Event.Type.PLAYER_INTERACT, this.playerListener, Event.Priority.Lowest, this);
	}

	public MobSpawnParams getUserParams(Player player) {
		if (this.playerState.containsKey(player)) {
			return this.playerState.get(player);
		}
		return new MobSpawnParams();
	}
	
	public void setUserParams(Player player, MobSpawnParams params) {
		if (this.playerState.containsKey(player)) {
			this.playerState.remove(player);
		}
		this.playerState.put(player, params);
	}
	
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args)
    {
		if (!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "Only players can use this command.");
		}

    	Player player = (Player) sender;
    	
		if (cmd.getName().compareToIgnoreCase("madmanmarkau_mobspawn") == 0) {
			if (args.length == 1) {
				if (!MobSpawnPermissions.has(player, "mobspawn.spawn")) return true;
				
				if (args[0].compareToIgnoreCase("off") == 0) {
					MobSpawnParams params = getUserParams(player);
					
					params.setEnabled(false);
					setUserParams(player, params);

					player.sendMessage(ChatColor.YELLOW + "MobSpawn is bow off.");
				} else {
					MobSpawnParams params = getUserParams(player);
					CreatureType creature = CreatureType.fromName(args[0]);
					
					if (creature == null) {
						player.sendMessage(ChatColor.RED + "Mob name not found.");
						return true;
					}
					
					params.setEnabled(true);
					params.setSelectedMob(creature);
					setUserParams(player, params);
					
					player.sendMessage(ChatColor.YELLOW + "MobSpawn set to " + creature.getName());
				}
				
				return true;
			}
		}
		return false;
    }
}
