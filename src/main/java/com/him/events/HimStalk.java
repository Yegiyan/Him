package com.him.events;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

import com.him.Him;
import com.him.entities.HerobrineEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class HimStalk
{	
	public static void stalk(ServerWorld world)
	{
		if (!world.getPlayers().isEmpty()) 
        {
            int playerIndex = new Random().nextInt(world.getPlayers().size());
            PlayerEntity player = world.getPlayers().get(playerIndex);
            
            // distance in blocks
            double minDistance = 32D;
            double maxDistance = 74D;
            double spawnDistance = ThreadLocalRandom.current().nextDouble(minDistance, maxDistance);
            BlockPos spawnPos = getSpawnPosition(player, spawnDistance);
            
            if (spawnPos != null) 
            {
                HerobrineEntity herobrine = Him.HEROBRINE.create(world);
                herobrine.refreshPositionAndAngles(spawnPos, 0, 0);
                world.spawnEntity(herobrine);
                Him.LOGGER.info("Herobrine stalking from " + spawnPos);
            }
        }
	}
	
	@SuppressWarnings("deprecation")
	private static boolean isValidSpawnPosition(World world, BlockPos pos) 
    {
        if (!world.getBlockState(pos.up()).isAir() || !world.getBlockState(pos.up(2)).isAir()) 
            return false;

        BlockState downState = world.getBlockState(pos.down());
        return downState.isSolid() && !downState.isOf(Blocks.WATER) && !downState.isOf(Blocks.LAVA);
    }
    
	private static BlockPos getSpawnPosition(PlayerEntity player, double distance) 
	{
	    for (int i = 0; i < 100; i++) 
	    {
	        // Get random angle between 0 and 2*pi
	        double angle = player.getRandom().nextDouble() * 2 * Math.PI;

	        // Calculate spawn position
	        double x = player.getX() + distance * Math.cos(angle);
	        double z = player.getZ() + distance * Math.sin(angle);
	        int y = player.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, (int)x, (int)z);
	        BlockPos pos = new BlockPos((int)x, (int)y, (int)z);

	        // If position is valid and is outside of the player's fov, return it
	        if (isValidSpawnPosition(player.getWorld(), pos) && !isPlayerLookingAtPosition(player, pos)) 
	            return pos;
	    }

	    return null;
	}

	private static boolean isPlayerLookingAtPosition(PlayerEntity player, BlockPos pos) 
	{
	    Vec3d toPos = new Vec3d(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D).subtract(player.getEyePos()).normalize();
	    Vec3d playerLookVec = player.getRotationVec(1.0F);
	    double dotProduct = toPos.dotProduct(playerLookVec);
	    return dotProduct > Math.cos(Math.toRadians(70));
	}
}