package net.madmanmarkau.MobSpawn;

import org.bukkit.entity.CreatureType;

public class MobSpawnParams {
	private boolean enabled = false;
	private CreatureType selectedMob = CreatureType.PIG;
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}

	public void setSelectedMob(CreatureType selectedMob) {
		this.selectedMob = selectedMob;
	}

	public CreatureType getSelectedMob() {
		return selectedMob;
	}
}
