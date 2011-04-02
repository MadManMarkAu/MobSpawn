package net.madmanmarkau.MobSpawn;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

public class BlockRayCaster {
	Player player;
	
	// Search parameters
	private double viewHeight = 1.62D;
	private double maxCastDistance = 300D;
	private double checkGranularity = 0.02D;
	private double startDistance = 0D;
	
	// Iteration parameters
	private Vector currentPos = new Vector();
	private Vector currentCheckVector = new Vector();
	private double currentCheckDistance = 0D;
	
	public void setViewHeight(double newValue) {
		this.viewHeight = newValue;
	}
	
	public double getViewHeight() {
		return this.viewHeight;
	}

	public void setMaxCastDistance(double newValue) {
		this.maxCastDistance = newValue;
	}
	
	public double getMaxCastDistance() {
		return this.maxCastDistance;
	}

	public void setCheckGranularity(double newValue) {
		this.checkGranularity = newValue;
	}
	
	public double getCheckGranularity() {
		return this.checkGranularity;
	}

	public void setStartDistance(double newValue) {
		this.startDistance = newValue;
	}
	
	public double getStartDistance() {
		return this.startDistance;
	}

	public BlockRayCaster(Player player, Location startLocation) {
		double xRot, yRot;
		
		this.player = player;
		
	    xRot = Math.toRadians((startLocation.getYaw() + 90.0F) % 360.0F);
	    yRot = Math.toRadians(startLocation.getPitch() * -1.0F);

	    // Follow along this vector when checking for blocks. Note, this vector is normalised.  
	    this.currentCheckVector.setY(Math.sin(yRot));
	    this.currentCheckVector.setX(Math.cos(yRot) * Math.cos(xRot));
	    this.currentCheckVector.setZ(Math.cos(yRot) * Math.sin(xRot));

	    this.currentPos = new Vector(startLocation.getX() + this.currentCheckVector.getX() * this.startDistance, startLocation.getY() + this.currentCheckVector.getY() * this.startDistance + viewHeight, startLocation.getZ() + this.currentCheckVector.getZ() * this.startDistance);
	}

	// Continue cast until new block reached
	public boolean moveNextBlock() {
		Vector lastPos = this.currentPos.clone();
		Vector newPos = new Vector();
		
		do {
			this.currentCheckDistance += this.checkGranularity;

			newPos.setX(this.currentPos.getX() + this.currentCheckVector.getX() * this.checkGranularity);
			newPos.setY(this.currentPos.getY() + this.currentCheckVector.getY() * this.checkGranularity);
			newPos.setZ(this.currentPos.getZ() + this.currentCheckVector.getZ() * this.checkGranularity);

			// Don't go out of the world. Doing so causes the world to cycle (over/underflow)
			if (newPos.getY() >= 128 || newPos.getY() < 0) {
				return false;
			}
			
			this.currentPos.setX(newPos.getX());
			this.currentPos.setY(newPos.getY());
			this.currentPos.setZ(newPos.getZ());
			
		} while ((this.currentCheckDistance <= this.maxCastDistance) &&
				(this.currentPos.getBlockX() == lastPos.getBlockX()) &&
				(this.currentPos.getBlockY() == lastPos.getBlockY()) &&
				(this.currentPos.getBlockZ() == lastPos.getBlockZ()));
		
/*		Messaging.send(player, "Cast: " + lastPos.getBlockX() + "," + lastPos.getBlockY() + "," + lastPos.getBlockZ() + " -> " + 
				this.currentPos.getBlockX() + "," + this.currentPos.getBlockY() + "," + this.currentPos.getBlockZ() + " (" + this.currentCheckDistance + ")");*/
		
		if (this.currentCheckDistance > this.maxCastDistance) {
			return false;
		}

		return true;
	}

	public Block getCurrentBlock(World world) {
		return world.getBlockAt(this.currentPos.getBlockX(), this.currentPos.getBlockY(), this.currentPos.getBlockZ());
	}
	
	public boolean runCastWithIgnore(World world, int[] ignoreList) {
		if (moveNextBlock()) {
			do {
				Block curBlock = getCurrentBlock(world);
				int curBlockId = curBlock.getTypeId();
				
				if (curBlockId != 0 &&
						curBlockId != 8 &&
						curBlockId != 9 &&
						curBlockId != 10 &&
						curBlockId != 11 &&
						curBlockId != 51) {
					return true;
				}
			} while (moveNextBlock());
		}
		
		return false;
	}
	
	public Vector getCurrentPos() {
		return this.currentPos;
	}
}
