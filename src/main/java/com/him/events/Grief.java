package com.him.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.him.Him;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FluidBlock;
import net.minecraft.block.LeavesBlock;
import net.minecraft.block.SignBlock;
import net.minecraft.block.WallRedstoneTorchBlock;
import net.minecraft.block.entity.SignBlockEntity;
import net.minecraft.block.entity.SignText;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.Heightmap;
import net.minecraft.world.World;

public class Grief
{
	public static void grief(ServerWorld world) 
	{
	    Random rand = new Random();
	    int decision = rand.nextInt(100) + 1;
	    
	    if (decision <= 10 && Him.CONFIG.enableSpawnTunnel) spawnTunnel(world);
	    else if (decision <= 20 && Him.CONFIG.enableSpawnShaft) spawnShaft(world);
	    else if (decision <= 35 && Him.CONFIG.enableSpawnDirtTower) spawnDirtTower(world);
	    else if (decision <= 50 && Him.CONFIG.enableSpawnSandPyramid) spawnSandPyramid(world);
	    else if (decision <= 70 && Him.CONFIG.enablePlaceSign) placeSign(world);
	    else if (decision <= 90 && Him.CONFIG.enablePlaceTorch) placeTorch(world);
	    else if (decision <= 95 && Him.CONFIG.enableTrimTrees) trimTrees(world);
	    else if (decision <= 100 && Him.CONFIG.enableSetFire) setFire(world);
	    
	    else Him.LOGGER.info("Herobrine didn't make a griefing decision!");
	}
	
	private static void spawnDirtTower(ServerWorld world) 
	{
	    int towerHeight = 6;
	    int distance = 128;

	    PlayerEntity player = world.getPlayers().get(new Random().nextInt(world.getPlayers().size()));
	    BlockPos playerPos = player.getBlockPos();

	    Random random = new Random();
	    BlockPos startPos = null;

	    for (int attempts = 0; attempts < 100; attempts++) 
	    {
	        startPos = playerPos.add(random.nextInt(distance * 2) - distance, 0, random.nextInt(distance * 2) - distance);
	        startPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, startPos);
	        Block startBlock = world.getBlockState(startPos.down()).getBlock();
	        
	        if (!(startBlock instanceof LeavesBlock) && !(startBlock instanceof FluidBlock)) 
	            break;
	        
	        if (attempts == 99) return;
	    }

	    for (int i = 0; i < towerHeight; i++) 
	    {
	        BlockPos towerPos = startPos.up(i);
	        world.setBlockState(towerPos, Blocks.COARSE_DIRT.getDefaultState(), 2);
	    }

	    Him.LOGGER.info("A Herobrine dirt tower has been spawned at coordinates: " + startPos);
	}
	
	private static void spawnShaft(ServerWorld world) 
	{
	    int playerIndex = new Random().nextInt(world.getPlayers().size());
	    PlayerEntity player = world.getPlayers().get(playerIndex);
	    BlockPos playerPos = player.getBlockPos();

	    // distance in blocks
	    int minDistance = 32;
	    int maxDistance = 64;
	    int shaftDepth = 64;

	    int startY;
	    BlockPos startPos = null;
	    List<BlockPos> validStartPositions = new ArrayList<>();

	    // make 10 attempts to find a valid starting position
	    for (int attempts = 0; attempts < 10; attempts++) 
	    {
	        validStartPositions.clear();
	        for (int x = playerPos.getX() - maxDistance; x <= playerPos.getX() + maxDistance; x++) 
	        {
	            for (int z = playerPos.getZ() - maxDistance; z <= playerPos.getZ() + maxDistance; z++) 
	            {
	                startY = new Random().nextInt(51); // random y-level below 50
	                startPos = new BlockPos(x, startY, z);

	                if (playerPos.getSquaredDistance(startPos) >= minDistance * minDistance && playerPos.getSquaredDistance(startPos) <= maxDistance * maxDistance && !world.getBlockState(startPos.down()).isAir()) 
	                    validStartPositions.add(startPos);
	            }
	        }

	        if (!validStartPositions.isEmpty()) 
	        {
	            startPos = validStartPositions.get(new Random().nextInt(validStartPositions.size()));
	            break;
	        }
	    }

	    if (startPos == null)
	        return;

	    for (int i = 0; i < shaftDepth; i++) 
	    {
	        BlockPos shaftPos = startPos.down(i);

	        // Set the 3x3 blocks to air
	        for (int dx = -1; dx <= 1; dx++) 
	        {
	            for (int dz = -1; dz <= 1; dz++) 
	            {
	                BlockPos airPos = shaftPos.add(dx, 0, dz);
	                world.setBlockState(airPos, Blocks.AIR.getDefaultState(), 2);
	            }
	        }

	        // chance to place redstone torch on the walls of the shaft (8% chance)
	        if (new Random().nextInt(100) < 8)
	        {
	            BlockPos torchPos = shaftPos.add(1, 0, 0); // replace with the direction you want the torch to face
	            BlockPos wallBlock = torchPos.add(1, 0, 0); // replace with the direction the torch is supposed to attach to
	            
	            if (world.getBlockState(torchPos).isAir() && !world.getBlockState(wallBlock).isAir())
	                world.setBlockState(torchPos, Blocks.REDSTONE_WALL_TORCH.getDefaultState().with(WallRedstoneTorchBlock.FACING, Direction.WEST), 3); // replace with the opposite direction of the torch face
	        }
	    }

	    Him.LOGGER.info("A Herobrine shaft has been spawned at coordinates: " + startPos);
	}
	
	private static void spawnTunnel(ServerWorld world) 
	{
	    List<Direction> horizontalDirections = Arrays.asList(Direction.NORTH, Direction.SOUTH, Direction.EAST, Direction.WEST);
	    Direction tunnelDirection = horizontalDirections.get(new Random().nextInt(horizontalDirections.size()));

	    int playerIndex = new Random().nextInt(world.getPlayers().size());
	    PlayerEntity player = world.getPlayers().get(playerIndex);
	    BlockPos playerPos = player.getBlockPos();

	    int minDistance = 32;
	    int maxDistance = 64;
	    int tunnelLength = 48;

	    int startY;
	    BlockPos startPos = null;
	    List<BlockPos> validStartPositions = new ArrayList<>();

	    // make 10 attempts to find a valid starting position
	    for (int attempts = 0; attempts < 10; attempts++) 
	    {
	        validStartPositions.clear();
	        for (int x = playerPos.getX() - maxDistance; x <= playerPos.getX() + maxDistance; x++) 
	        {
	            for (int z = playerPos.getZ() - maxDistance; z <= playerPos.getZ() + maxDistance; z++) 
	            {
	                startY = world.getTopY(Heightmap.Type.WORLD_SURFACE, x, z);
	                startPos = new BlockPos(x, startY, z);

	                if (playerPos.getSquaredDistance(startPos) >= minDistance * minDistance && playerPos.getSquaredDistance(startPos) <= maxDistance * maxDistance && !world.getBlockState(startPos.offset(tunnelDirection)).isAir()) 
	                    validStartPositions.add(startPos);
	            }
	        }

	        if (!validStartPositions.isEmpty()) 
	        {
	            startPos = validStartPositions.get(new Random().nextInt(validStartPositions.size()));
	            break;
	        }
	    }

	    if (startPos == null)
	        return;

	    Direction sideDirection = tunnelDirection.rotateYClockwise();

	    for (int i = 0; i < tunnelLength; i++) {
	        BlockPos tunnelPos = startPos.offset(tunnelDirection, i);

	        // ignore isAir() the first and last 15% of the tunnel
	        if (i > tunnelLength * 0.15F && i < tunnelLength * 0.85F) 
	            if (world.getBlockState(tunnelPos.up(2)).isAir() || world.getBlockState(tunnelPos.up(2).offset(sideDirection)).isAir())
	                continue;

	        world.setBlockState(tunnelPos, Blocks.AIR.getDefaultState(), 2);
	        world.setBlockState(tunnelPos.up(), Blocks.AIR.getDefaultState(), 2);
	        world.setBlockState(tunnelPos.offset(sideDirection), Blocks.AIR.getDefaultState(), 2);
	        world.setBlockState(tunnelPos.up().offset(sideDirection), Blocks.AIR.getDefaultState(), 2);
	        
	        // chance to place redstone torch on the walls of the tunnel (7% chance)
	        if (new Random().nextInt(100) < 7)
	        { 
	            BlockPos torchPos = tunnelPos.offset(sideDirection).up();
	            BlockPos wallBlock = tunnelPos.offset(sideDirection).offset(sideDirection).up();
	            
	            if (world.getBlockState(torchPos).isAir() && !world.getBlockState(wallBlock).isAir())
	                world.setBlockState(torchPos, Blocks.REDSTONE_WALL_TORCH.getDefaultState().with(WallRedstoneTorchBlock.FACING, sideDirection.getOpposite()), 3);
	        }
	    }

	    Him.LOGGER.info("A Herobrine tunnel has been spawned at coordinates: " + startPos);
	}
	
	private static void placeTorch(ServerWorld world) 
	{
	    int playerIndex = new Random().nextInt(world.getPlayers().size());
	    PlayerEntity player = world.getPlayers().get(playerIndex);
	    BlockPos playerPos = player.getBlockPos();

	    // distance in blocks
	    int minDistance = 16;
	    int maxDistance = 64;

	    int distanceX, distanceZ, distanceY;
	    BlockPos torchPos;
	    boolean torchPlaced;

	    do
	    {
	        torchPlaced = false;
	        distanceX = new Random().nextInt(maxDistance * 2 + 1) - maxDistance;
	        distanceZ = new Random().nextInt(maxDistance * 2 + 1) - maxDistance;
	        distanceY = new Random().nextInt(maxDistance * 2 + 1) - maxDistance;
	        torchPos = new BlockPos(playerPos.getX() + distanceX, playerPos.getY() + distanceY, playerPos.getZ() + distanceZ);

	        double distance = Math.sqrt(distanceX * distanceX + distanceY * distanceY + distanceZ * distanceZ);
	        if (distance < minDistance)
	            continue;

	        // ensure torchPos is occupied by air
	        if (!world.getBlockState(torchPos).isAir())
	            continue;

	        Direction[] directions = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST}; // the directions that a wall torch can face

	        // try to place a wall torch if possible
	        for(Direction direction : directions)
	        {
	            BlockPos blockAdjacent = torchPos.offset(direction.getOpposite()); 
	            BlockState blockStateAdjacent = world.getBlockState(blockAdjacent);

	            if(blockStateAdjacent.isFullCube(world, blockAdjacent))
	            {
	                world.setBlockState(torchPos, Blocks.REDSTONE_WALL_TORCH.getDefaultState().with(WallRedstoneTorchBlock.FACING, direction), 3);
	                Him.LOGGER.info("Herobrine placed a redstone torch on a wall at " + torchPos);
	                torchPlaced = true;
	                break;
	            }
	        }

	        // if it wasn't possible to place a wall torch, try to place a normal redstone torch
	        if (!torchPlaced)
	        {
	            BlockPos blockBelow = torchPos.down(); 
	            BlockState blockStateBelow = world.getBlockState(blockBelow);

	            if(blockStateBelow.isFullCube(world, blockBelow))
	            {
	                world.setBlockState(torchPos, Blocks.REDSTONE_TORCH.getDefaultState(), 3);
	                Him.LOGGER.info("Herobrine placed a redstone torch on the ground at " + torchPos);
	                torchPlaced = true;
	            }
	        }
	    } 
	    while (!torchPlaced);
	}

	private static void placeSign(ServerWorld world) 
	{
	    int playerIndex = new Random().nextInt(world.getPlayers().size());
	    PlayerEntity player = world.getPlayers().get(playerIndex);
	    
	    BlockPos playerPos = player.getBlockPos();
	    int playerX = playerPos.getX();
	    int playerZ = playerPos.getZ();

	    // distance in blocks
	    int minDistance = 16;
	    int maxDistance = 64;

	    BlockPos pos = null;
	    BlockState blockBelow = null;
	    do 
	    {
	        int distanceX = new Random().nextInt(maxDistance - minDistance + 1) + minDistance;
	        int distanceZ = new Random().nextInt(maxDistance - minDistance + 1) + minDistance;

	        pos = new BlockPos(playerX + distanceX, player.getWorld().getTopY(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, playerX + distanceX, playerZ + distanceZ), playerZ + distanceZ);
	        blockBelow = world.getBlockState(pos.down());
	    } 
	    while (blockBelow.isAir() || blockBelow.isOf(Blocks.WATER));

	    String[] MESSAGES = new String[] 
				{
					"I will be your end",
					"Sleep well tonight",
				    "Defiance is futile",
				    "Can't outrun fate",
				    "Can't run away",
				    "You're abandoned",
				    "I'm watching you",
				    "You're all alone",
				    "Accept your fate",
				    "This is my world",
				    "The void awaits",
				    "Won't be saved",
				    "Just the start",
				    "Coming for you",
				    "I have awoken",
				    "Just a pawn",
				    "Beware him",
				    "No way out",
				    "No escape",
				    "I see you",
				    "Wake up",
				};
	    
	    // set the block at the specified position to an oak sign, with a random orientation
	    BlockState signState = Blocks.OAK_SIGN.getDefaultState().with(SignBlock.ROTATION, new Random().nextInt(16));
	    world.setBlockState(pos, signState, 3);

	    // get the block entity at that position
	    SignBlockEntity sign = (SignBlockEntity)world.getBlockEntity(pos);

	    // if the block entity is a sign, set the text on it
	    if (sign != null) 
	    {
	        String message = MESSAGES[new Random().nextInt(MESSAGES.length)];
	        int line = (int) (Math.random() * 4);
	        sign.setText(new SignText().withMessage(line, Text.literal(message)), false);
	        sign.markDirty();
	    }

	    Him.LOGGER.info("Herobrine left a sign at " + pos);
	}
	
	private static void setFire(ServerWorld world) 
	{
	    int playerIndex = new Random().nextInt(world.getPlayers().size());
	    PlayerEntity player = world.getPlayers().get(playerIndex);

	    // distance in blocks
	    int minDistance = 16;
	    int maxDistance = 64;

	    BlockPos playerPos = player.getBlockPos();
	    int playerX = playerPos.getX();
	    int playerZ = playerPos.getZ();

	    int distanceX, distanceZ;

	    for(int i = 0; i < 100; i++)
	    {
	        distanceX = new Random().nextInt(maxDistance * 2 + 1) - maxDistance;
	        distanceZ = new Random().nextInt(maxDistance * 2 + 1) - maxDistance;

	        double distance = Math.sqrt(distanceX * distanceX + distanceZ * distanceZ);
	        if (distance < minDistance)
	            continue;

	        BlockPos startPos = new BlockPos(playerX + distanceX, 0, playerZ + distanceZ);
	        BlockPos endPos = new BlockPos(playerX + distanceX, world.getHeight(), playerZ + distanceZ);

	        for (BlockPos pos : BlockPos.iterate(startPos, endPos)) 
	        {
	            BlockState state = world.getBlockState(pos);
	            if (state.isIn(BlockTags.LOGS)) 
	            {
	                world.setBlockState(pos, Blocks.FIRE.getDefaultState(), 3);
	                Him.LOGGER.info("Herobrine set fire to a log at " + pos);
	                return;
	            }
	        }
	    }
	}
	
	private static void trimTrees(ServerWorld world) 
	{
		int playerIndex = new Random().nextInt(world.getPlayers().size());
        PlayerEntity player = world.getPlayers().get(playerIndex);

        // distance in chunks
        int maxDistance = 12;
        int minDistance = 4;
        int dx, dz;

        do 
        {
            dx = new Random().nextInt(maxDistance * 2 + 1) - maxDistance;
            dz = new Random().nextInt(maxDistance * 2 + 1) - maxDistance;
        } 
        while (Math.sqrt(dx * dx + dz * dz) < minDistance);

        int chunkX = ((int) player.getX() >> 4) + dx;
        int chunkZ = ((int) player.getZ() >> 4) + dz;

        BlockPos centerPos = new BlockPos((chunkX + 2) << 4, 70, (chunkZ + 2) << 4);
        Him.LOGGER.info("Trimming trees at " + centerPos);
        
        for (int offsetX = 0; offsetX <= 3; offsetX++) 
        {
            for (int offsetZ = 0; offsetZ <= 3; offsetZ++) 
            {
                for (int x = 0; x < 16; x++) 
                {
                    for (int z = 0; z < 16; z++) 
                    {
                        for (int y = 0; y < world.getHeight(); y++) 
                        {
                            BlockPos pos = new BlockPos(x + ((chunkX + offsetX) << 4), y, z + ((chunkZ + offsetZ) << 4));
                            BlockState state = world.getBlockState(pos);
                            
                            if (state.isIn(BlockTags.LEAVES)) 
                                world.setBlockState(pos, Blocks.AIR.getDefaultState(), 2);
                        }
                    }
                }
            }
        }
    }
	
	private static void spawnSandPyramid(ServerWorld world)
	{
	    int playerIndex = new Random().nextInt(world.getPlayers().size());
	    PlayerEntity player = world.getPlayers().get(playerIndex);
	    
	    BlockPos spawnPos = getValidWaterPos(player, 64, 256);
	    
	    if (spawnPos != null) 
	    {
	        createSandPyramid(world, spawnPos);
	        Him.LOGGER.info("Herobrine created a sand pyramid at " + spawnPos);
	    }
	    
	    else 
	        Him.LOGGER.info("Could not find a valid position for a sand pyramid.");
	}

	private static BlockPos getValidWaterPos(PlayerEntity player, int minDistance, int maxDistance)
	{
		// try 100 times to find a valid position
	    for (int attempts = 0; attempts < 100; attempts++) 
	    {
	        double angle = player.getRandom().nextDouble() * 2 * Math.PI;
	        double radius = minDistance + player.getRandom().nextDouble() * (maxDistance - minDistance);
	        double x = player.getX() + radius * Math.cos(angle);
	        double z = player.getZ() + radius * Math.sin(angle);
	        int y = player.getWorld().getTopY(Heightmap.Type.OCEAN_FLOOR, (int)x, (int)z);
	        BlockPos pos = new BlockPos((int)x, (int)y, (int)z);
	        
	        if (isValidPyramidPos(player.getWorld(), pos)) 
	            return pos;
	    }
	    return null;
	}

	private static boolean isValidPyramidPos(World world, BlockPos pos)
	{
	    BlockState currentBlockState = world.getBlockState(pos);
	    BlockState belowBlockState = world.getBlockState(pos.down());

	    // check if the base is water and block below base is solid
	    if (currentBlockState.getBlock() != Blocks.WATER || !belowBlockState.isSideSolidFullSquare(world, pos.down(), Direction.UP))
	        return false;

	    // check the surrounding blocks at base level for water
	    Block[] surroundingBlocksAtBase = new Block[]
	    {
	            world.getBlockState(pos.north()).getBlock(),
	            world.getBlockState(pos.south()).getBlock(),
	            world.getBlockState(pos.east()).getBlock(),
	            world.getBlockState(pos.west()).getBlock()
	    };

	    for (Block block : surroundingBlocksAtBase) 
	        if (block != Blocks.WATER)
	            return false;

	    // check the blocks above the base for air
	    for (int dy = 1; dy <= 3; dy++) 
	    {
	        Block aboveBlock = world.getBlockState(pos.up(dy)).getBlock();
	        if (aboveBlock != Blocks.AIR)
	            return false;
	    }

	    // check that all 25 blocks under the 5x5 base are solid
	    for (int dx = -2; dx <= 2; dx++) 
	    {
	        for (int dz = -2; dz <= 2; dz++) 
	        {
	            BlockState underBlockState = world.getBlockState(pos.add(dx, -1, dz));
	            if (!underBlockState.isSideSolidFullSquare(world, pos.add(dx, -1, dz), Direction.UP))
	                return false;
	        }
	    }

	    // check that all 24 blocks around the 5x5 base are water
	    int[] dx = {-3, -3, -3, -2, 2, 3, 3, 3, 2, -2, -2, -1, 1, 2, -2, -1, 1, 2, -1, -1, 1, 1, 0, 0};
	    int[] dz = {-2, 0, 2, 3, 3, 2, 0, -2, -3, -3, -1, -2, -2, -1, 1, 2, 2, 1, -3, 3, -3, 3, -3, 3};

	    for (int i = 0; i < 24; i++) 
	    {
	        Block surroundBlock = world.getBlockState(pos.add(dx[i], 0, dz[i])).getBlock();
	        if (surroundBlock != Blocks.WATER)
	            return false;
	    }
	    return true;
	}

	private static void createSandPyramid(World world, BlockPos pos) 
	{
	    // create a 5x5 base in the water
	    for (int x = -2; x <= 2; x++) 
	    {
	        for (int z = -2; z <= 2; z++) 
	        {
	            BlockPos newPos = pos.add(x, 0, z);
	            world.setBlockState(newPos, Blocks.SAND.getDefaultState());
	        }
	    }
	    
	    // create a 3x3 on top of the base
	    for (int x = -1; x <= 1; x++) 
	    {
	        for (int z = -1; z <= 1; z++) 
	        {
	            BlockPos newPos = pos.add(x, 1, z);
	            world.setBlockState(newPos, Blocks.SAND.getDefaultState());
	        }
	    }
	    
	    // add a single block on top
	    BlockPos topPos = pos.up(2);
	    world.setBlockState(topPos, Blocks.SAND.getDefaultState());
	}
}
