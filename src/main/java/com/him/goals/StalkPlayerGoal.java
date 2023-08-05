package com.him.goals;

import java.util.EnumSet;

import com.him.entities.HerobrineEntity;

import net.minecraft.entity.Entity.RemovalReason;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class StalkPlayerGoal extends Goal 
{
    private final HerobrineEntity herobrine;
    private float stalkedPlayerDistance;
    private float runDistance;
    private int stareTime;
    private int runTime;
    private PlayerEntity targetPlayer;

    public StalkPlayerGoal(HerobrineEntity herobrine, float stalkedPlayerDistance, float runDistance) 
    {
        this.herobrine = herobrine;
        this.stalkedPlayerDistance = stalkedPlayerDistance;
        this.runDistance = runDistance;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
    }

    public void updateStalkDistance(float newDistance) 
    {
        this.stalkedPlayerDistance = newDistance;
    }
    
    @Override
    public boolean canStart() 
    {
    	this.targetPlayer = null;
        this.targetPlayer = this.herobrine.getWorld().getClosestPlayer(this.herobrine, stalkedPlayerDistance);
        return this.targetPlayer != null && this.herobrine.canSee(this.targetPlayer);
    }

    @Override
    public void start() 
    {
        this.stareTime = 0;
        this.runTime = 0;
    }

	@Override
    public void stop() 
    {
        this.targetPlayer = null;
        this.herobrine.getNavigation().stop();
     
        // If the current world is a server world, create particles
        if (this.herobrine.getWorld() instanceof ServerWorld) 
        {
            ((ServerWorld)this.herobrine.getWorld()).spawnParticles(ParticleTypes.END_ROD, 
                this.herobrine.getX(), 
                this.herobrine.getY(), 
                this.herobrine.getZ(), 
                30,     // Number of particles
                0.25D,  // Spawn width
                0.25D,  // Spawn height
                0.25D,  // Spawn depth
                0.02D); // Speed of particles
        }
        
        // Remove Herobrine entity
        this.herobrine.remove(RemovalReason.DISCARDED);
    }
    
    @Override
    public boolean shouldContinue() 
    {
        if (this.runTime > 100) 
            return false;

        return this.herobrine.canSee(this.targetPlayer) || !this.herobrine.getNavigation().isIdle();
    }

    @Override
    public void tick() 
    {
        if (this.stareTime <= 240) // 240 ticks = 12 seconds
        {
            if (this.herobrine.canSee(this.targetPlayer)) 
            {
                this.stareTime++;
                    
                if (this.stareTime > 40) // 40 ticks = 2 seconds
                    this.runAway();
            } 
            
            else 
            {
            	this.stareTime = 0;
                this.runTime = 0;
            }
        }
    }

    private void runAway() 
    {
        Vec3d runPosition = this.getRunAwayPosition(this.targetPlayer, this.herobrine, this.runDistance);
        this.herobrine.getNavigation().startMovingTo(runPosition.x, runPosition.y, runPosition.z, 0.70D);
        
        if (!isPlayerLookingAtHerobrine(targetPlayer, herobrine)) 
            this.stop();	
    }

    private Vec3d getRunAwayPosition(PlayerEntity player, HerobrineEntity herobrine, double distance) 
    {
        double dx = herobrine.getX() - player.getX();
        double dz = herobrine.getZ() - player.getZ();
        double magnitude = Math.sqrt(dx * dx + dz * dz);
        double desiredX = herobrine.getX() + dx / magnitude * distance;
        double desiredZ = herobrine.getZ() + dz / magnitude * distance;
        return new Vec3d(desiredX, herobrine.getY(), desiredZ);
    }
    
    private boolean isPlayerLookingAtHerobrine(PlayerEntity player, HerobrineEntity herobrine) 
    {
        Vec3d toHerobrine = herobrine.getEyePos().subtract(player.getEyePos()).normalize();
        Vec3d playerLookVec = player.getRotationVec(1.0F);
        double dotProduct = toHerobrine.dotProduct(playerLookVec);
        return dotProduct > 0.0F; // -1 to 1
    }
}