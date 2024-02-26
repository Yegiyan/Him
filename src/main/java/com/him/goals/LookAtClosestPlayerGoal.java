package com.him.goals;

import java.util.List;

import com.him.entities.HerobrineEntity;

import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.player.PlayerEntity;

public class LookAtClosestPlayerGoal extends Goal 
{
    private final HerobrineEntity herobrine;
    private PlayerEntity targetPlayer;

    public LookAtClosestPlayerGoal(HerobrineEntity herobrine) 
    {
        this.herobrine = herobrine;
    }

    @Override
    public boolean canStart()
    {
        List<? extends PlayerEntity> list = this.herobrine.getWorld().getPlayers();
        double closestDist = Double.MAX_VALUE;
        for (PlayerEntity player : list)
        {
            if (player instanceof PlayerEntity)
            {
                PlayerEntity playerEntity = (PlayerEntity) player;
                double dist = this.herobrine.squaredDistanceTo(playerEntity);
                if (dist < closestDist && herobrine.canSee(playerEntity))
                {
                    this.targetPlayer = playerEntity;
                    closestDist = dist;
                }
            }
        }
        return this.targetPlayer != null;
    }

    @Override
    public void start() 
    {
        this.herobrine.getLookControl().lookAt(this.targetPlayer, 10.0F, (float)this.herobrine.getMaxLookPitchChange());
    }

    @Override
    public void stop() 
    {
        this.targetPlayer = null;
    }

    @Override
    public void tick() 
    {
        if(this.targetPlayer != null && herobrine.canSee(this.targetPlayer)) 
        {
            this.herobrine.getLookControl().lookAt(this.targetPlayer, 10.0F, (float)this.herobrine.getMaxLookPitchChange());
        }
        else
        {
            this.stop();
        }
    }
}