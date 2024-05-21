package com.him.entities;

import com.him.Him;
import com.him.goals.LookAtClosestPlayerGoal;
import com.him.goals.StalkPlayerGoal;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.world.World;

public class HerobrineEntity extends PathAwareEntity 
{
	private int ticksVanish = -1;
	
    public HerobrineEntity(EntityType<? extends PathAwareEntity> entityType, World world) 
    {
        super(entityType, world);
    }

    @Override
    protected void initGoals() 
    {
    	this.goalSelector.add(0, new StalkPlayerGoal(this, 64F, 16F));
    	this.goalSelector.add(1, new LookAtClosestPlayerGoal(this));
        this.goalSelector.add(2, new SwimGoal(this));
    }
    
    @Override
    public boolean damage(DamageSource source, float amount)
    {
        return false;
    }
    
    @Override
    public void tick() 
    {
    	super.tick();

        if (ticksVanish >= 0)
        {
        	ticksVanish++;
            if (ticksVanish >= 30)
                this.remove(RemovalReason.DISCARDED);
        }
        
        else
            this.checkForNearbyPlayer();
    }
    
    private void checkForNearbyPlayer()
    {
        PlayerEntity closestPlayer = this.getWorld().getClosestPlayer(this, 24D);

        if (closestPlayer != null)
        {
            World world = closestPlayer.getWorld();
            closestPlayer.addStatusEffect(new StatusEffectInstance(StatusEffects.DARKNESS, 60, 1));
            
            if (!world.isClient)
                world.playSound(null, closestPlayer.getX(), closestPlayer.getY(), closestPlayer.getZ(), Him.SIGHTING, SoundCategory.AMBIENT, 0.75F, 1F);
            
            ticksVanish = 0;
        }
    }
}