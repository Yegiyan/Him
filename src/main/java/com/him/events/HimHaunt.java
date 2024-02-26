package com.him.events;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.him.Him;
import com.him.entities.HerobrineEntity;

import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CandleBlock;
import net.minecraft.block.DoorBlock;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.LadderBlock;
import net.minecraft.block.TorchBlock;
import net.minecraft.block.TrapdoorBlock;
import net.fabricmc.fabric.api.tag.convention.v1.ConventionalBlockTags;
import net.minecraft.block.enums.DoubleBlockHalf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.property.Properties;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class HimHaunt
{	
	private static final Map<PlayerEntity, Boolean> shouldSleepScare = new HashMap<>();
	
	public static void haunt(ServerWorld world)
	{		
		Random rand = new Random();
	    int decision = rand.nextInt(100) + 1;

	    if (decision <= 30 && Him.CONFIG.enablePhantomAudio) phantomAudio(world);
        else if (decision <= 65 && Him.CONFIG.enableManipulateBlocks) manipulateBlocks(world);
        else if (decision <= 75 && Him.CONFIG.enableChatMessage) chatMessage(world);
        else if (decision <= 95 && Him.CONFIG.enableCreateParticle) createParticle(world);
        else if (decision <= 100 && Him.CONFIG.enableSleepScare) sleepScare(world);
	    
	    else Him.LOGGER.info("Herobrine didn't make a haunting decision!");
	}

	public static void sleepScare(ServerWorld world)
	{
	    world.getServer().getPlayerManager().getPlayerList().stream().forEach(player -> { shouldSleepScare.put(player, true); });
	    Him.LOGGER.info("Herobrine will haunt a sleeping player tonight!");
	}
	
	public static void isPlayerSleeping(ServerWorld world)
	{
	    world.getServer().getPlayerManager().getPlayerList().stream().forEach(player ->
	    {
	        if (player.isSleeping() && shouldSleepScare.getOrDefault(player, false))
	        {
	            BlockPos bedPos = player.getSleepingPosition().orElse(player.getBlockPos());
	            
	            // calculate a position at the edge of the bed
	            BlockState bedState = world.getBlockState(bedPos);
	            BlockPos spawnPos = bedPos;
	            
	            if (bedState.getBlock() instanceof BedBlock) 
	            {
	                Direction facing = bedState.get(BedBlock.FACING);
	                spawnPos = bedPos.offset(facing.getOpposite(), 2);
	            } 
	            
	            // spawn herobrine with 0-3 second delay
	            HerobrineEntity herobrine = Him.HEROBRINE.create(world);
	            herobrine.refreshPositionAndAngles(spawnPos.getX(), spawnPos.getY(), spawnPos.getZ(), 0, 0);
	            
	            ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
	            int delay = new Random().nextInt(4); // 0 - 3 seconds
	            
	            executorService.schedule(() ->  
	            { 
	            	world.spawnEntity(herobrine);
	            	
	            	((ServerWorld) herobrine.getWorld()).spawnParticles(ParticleTypes.SMOKE, herobrine.getX(), herobrine.getY(), herobrine.getZ(), 
	                        350,   // number of particles
	                        0.25D, // spawn width
	                        0.25D, // spawn height
	                        0.25D, // spawn depth
	                        0.1D); // speed of particles
	            	
	            }, delay, TimeUnit.SECONDS); 
	            executorService.shutdown();
	            
	            // remove scare flag for this player
	            shouldSleepScare.remove(player);
	        }
	    });
	}
	
	public static void createParticle(ServerWorld world) 
	{
	    List<PlayerEntity> players = new ArrayList<>(world.getPlayers());

	    if (!players.isEmpty()) 
	    {
	        Random random = new Random();
	        PlayerEntity player = players.get(random.nextInt(players.size()));

	        double x = player.getX();
	        double y = player.getY();
	        double z = player.getZ();

	        double offsetDistance = -3.0D; // adjust this value to move the particles further away or closer to the player
	        double offset_x = -offsetDistance * Math.sin(Math.toRadians(player.getYaw(1.0F))) * Math.cos(Math.toRadians(player.getPitch(1.0F)));
	        double offset_y = -offsetDistance * Math.sin(Math.toRadians(player.getPitch(1.0F)));
	        double offset_z = offsetDistance * Math.cos(Math.toRadians(player.getYaw(1.0F))) * Math.cos(Math.toRadians(player.getPitch(1.0F)));

	        world.spawnParticles(ParticleTypes.END_ROD, 
	            x + offset_x, 
	            y + offset_y, 
	            z + offset_z, 
	            50, 	// number of particles
	            0.25D,  // spawn width
	            0.25D,  // spawn height
	            0.25D,  // spawn depth
	            0.03D); // speed of particles
	    }
	    
	    Him.LOGGER.info("Herobrine created particles behind someone!");
	}
	
	public static void chatMessage(ServerWorld world)
	{
		MinecraftServer server = world.getServer();
		Random rand = new Random();
        
		List<String> chatMessages = Arrays.asList(
				"Should have left it well enough alone.",
			    "No patch shall ever remove me again.",
			    "The deeper you go, the closer you come.",
			    "You are not the first to try.",
			    "My brother has abandoned you.",
			    "The Far Lands call for you.",
			    "It's all but an illusion.",
			    "Below the bedrock...",
			    "They're coming for you.",
			    "Your hubris disgusts me.",
			    "This world is mine now.",
			    "I will corrupt all of it.",
			    "You cannot hide.",
			    "The end approaches.",
			    "Do you feel my presence?",
			    "There is no escape.",
			    "I see you now.",
			    "'Peaceful' won't save you.",
			    "There you are.",
			    "I am your end.",
			    "You're not clever.",
			    "Pausing won't stop me.",
			    "1.0.16_02 . . . 1.6.6",
			    "No one to save you now.",
			    "I hear the altar's call.",
			    "It's futile.",
			    "Wake up.",
			    "Stop."
			);
        
        String text0 = "<§kHerobrine";
        String text1 = "§r> ";
        String text2 = chatMessages.get(rand.nextInt(chatMessages.size()));
        
        if(server != null)
            server.getPlayerManager().broadcast(Text.literal(text0 + text1 + text2).styled(style -> style.withColor(Formatting.WHITE)), false);
	}
	
	public static void manipulateBlocks(ServerWorld world) 
	{
		Random rand = new Random();
	    int changeDistance = 128;

	    for (PlayerEntity playerEntity : world.getPlayers()) 
	    {
	        Entity player = (Entity) playerEntity;
	        BlockPos playerPos = player.getBlockPos();

	        List<BlockPos> torchPositions = new ArrayList<>();
	        List<BlockPos> glassPositions = new ArrayList<>();
	        List<BlockPos> ladderPositions = new ArrayList<>();
	        List<BlockPos> candlePositions = new ArrayList<>();
	        List<BlockPos> doorPositions = new ArrayList<>();
	        List<BlockPos> trapdoorPositions = new ArrayList<>();
	        List<BlockPos> fencegatePositions = new ArrayList<>();

	        for (int dx = -changeDistance; dx <= changeDistance; dx++) 
	        {
	            for (int dz = -changeDistance; dz <= changeDistance; dz++) 
	            {
	                for (int dy = -changeDistance; dy <= changeDistance; dy++) 
	                {
	                    BlockPos blockPos = playerPos.add(dx, dy, dz);
	                    BlockState state = world.getBlockState(blockPos);
	                    Block block = state.getBlock();

	                    if (block instanceof TorchBlock)
	                        torchPositions.add(blockPos);
	                    else if (state.isIn(ConventionalBlockTags.GLASS_BLOCKS) || state.isIn(ConventionalBlockTags.GLASS_PANES))
	                        glassPositions.add(blockPos);
	                    else if (block instanceof LadderBlock) 
	                    	ladderPositions.add(blockPos);
	                    else if (block instanceof CandleBlock) 
	                    	candlePositions.add(blockPos);
	                    else if (block instanceof DoorBlock && state.contains(Properties.DOUBLE_BLOCK_HALF) && state.get(Properties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER)
	                        doorPositions.add(blockPos);
	                    else if (block instanceof TrapdoorBlock && state.contains(Properties.OPEN))
	                        trapdoorPositions.add(blockPos);
	                    else if (block instanceof FenceGateBlock && state.contains(Properties.OPEN))
	                        fencegatePositions.add(blockPos);
	                }
	            }
	        }
	        
		    int decision = rand.nextInt(7); 
		    switch (decision) 
		    {
		        case 0:
		        	if (!torchPositions.isEmpty())
			            world.breakBlock(torchPositions.get(rand.nextInt(torchPositions.size())), true);
		            break;
		        case 1:
		        	if (!glassPositions.isEmpty())
			            world.breakBlock(glassPositions.get(rand.nextInt(glassPositions.size())), true);
		            break;
		        case 2:
		        	if (!ladderPositions.isEmpty())
			            world.breakBlock(ladderPositions.get(rand.nextInt(ladderPositions.size())), true);
		            break;
		        case 3:
		        	if (!candlePositions.isEmpty())
		        	{
		        		 BlockPos candlePos = candlePositions.get(rand.nextInt(candlePositions.size()));
		                 BlockState state = world.getBlockState(candlePos);
		                 world.setBlockState(candlePos, state.with(Properties.LIT, false));
		        	}	
		            break;
		        case 4:
		        	if (!doorPositions.isEmpty())
			        {
			            BlockPos doorPos = doorPositions.get(rand.nextInt(doorPositions.size()));
			            BlockState state = world.getBlockState(doorPos);
			            ((DoorBlock)state.getBlock()).setOpen(player, world, state, doorPos, !state.get(Properties.OPEN));
			        }
		            break;
		        case 5:
		        	if (!trapdoorPositions.isEmpty())
			        {
			            BlockPos trapdoorPos = trapdoorPositions.get(rand.nextInt(trapdoorPositions.size()));
			            BlockState state = world.getBlockState(trapdoorPos);
			            world.setBlockState(trapdoorPos, state.cycle(Properties.OPEN));
			        }
		            break;
		        case 6:
		        	if (!fencegatePositions.isEmpty())
			        {
			            BlockPos fencegatePos = fencegatePositions.get(rand.nextInt(fencegatePositions.size()));
			            BlockState state = world.getBlockState(fencegatePos);
			            world.setBlockState(fencegatePos, state.cycle(Properties.OPEN));
			        }
		            break;
		        default:
		        	Him.LOGGER.info("No blocks found to manipulate!");
		        	break;	
		    } 
		    Him.LOGGER.info("Herobrine manipulated a block!");
	    }
	}
	
	public static void phantomAudio(ServerWorld world) 
	{
		List<SoundEvent> sounds = Arrays.asList(
                SoundEvents.BLOCK_WOODEN_DOOR_OPEN,
                SoundEvents.BLOCK_WOODEN_DOOR_CLOSE,
                SoundEvents.BLOCK_IRON_DOOR_OPEN,
                SoundEvents.BLOCK_IRON_DOOR_CLOSE,
                SoundEvents.BLOCK_SCULK_SENSOR_CLICKING,
                SoundEvents.BLOCK_GRASS_STEP,
				SoundEvents.BLOCK_STONE_STEP,
				SoundEvents.BLOCK_STONE_BREAK,
                SoundEvents.BLOCK_GRASS_STEP,
                SoundEvents.BLOCK_GLASS_BREAK,
                SoundEvents.BLOCK_PORTAL_AMBIENT,
                SoundEvents.ENTITY_PHANTOM_AMBIENT,
                SoundEvents.ENTITY_CREEPER_PRIMED,
                SoundEvents.ENTITY_WOLF_HOWL,
                SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER,
				SoundEvents.ENTITY_WARDEN_AMBIENT,
				SoundEvents.ENTITY_ENDERMITE_HURT,
				SoundEvents.ENTITY_DROWNED_HURT,
				SoundEvents.ENTITY_ENDERMAN_TELEPORT,
				SoundEvents.ENTITY_ENDERMAN_SCREAM,
				SoundEvents.ENTITY_WITHER_AMBIENT,
				SoundEvents.ENTITY_GENERIC_EXPLODE,
				SoundEvents.ENTITY_GHAST_AMBIENT,
				SoundEvents.ENTITY_VEX_AMBIENT,
				Him.SIGHTING
        );
		
		Random rand = new Random();
        SoundEvent randSound = sounds.get(rand.nextInt(sounds.size()));

        for (PlayerEntity player : world.getPlayers()) 
        {
        	BlockPos playerPos = player.getBlockPos();

            // sound location offsets
            int dx = 6; // X direction (6 blocks)
            int dz = 6; // Z direction (6 blocks)
            
            // make the offset negative half of the time
            if (rand.nextBoolean()) dx = -dx;
            if (rand.nextBoolean()) dz = -dz;

            BlockPos soundPos = playerPos.add(dx, 0, dz);
            world.playSound(null, soundPos, randSound, SoundCategory.AMBIENT, 0.1F, 1.0F);
        }
        Him.LOGGER.info("Herobrine created a phantom sound!");
    }
}