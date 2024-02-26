package com.him.events;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.him.Him;

import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Formatting;
import net.minecraft.util.WorldSavePath;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Altar 
{   
    private static final String HIM_ALTAR_STATE = "data/HimAltar.properties";
    private static final String HIM_GREET_STATE = "data/HimGreet.properties";

    public static void curseWorld(ServerWorld world)
    {
    	MinecraftServer server = world.getServer();
		Random rand = new Random();
    	
    	List<String> chatMessages = Arrays.asList(
				"Come then, I'll be waiting.",
				"You've made a terrible mistake.",
				"No turning back now.",
				"At last, another arrives.",
				"You've come to a doomed world.",
				"You will find no escape from this world.",
				"Gone are you from my brother's watchful eye.",
				"This world is a void that will consume you.",
				"You're not the first here, nor will you be the last.",
				"Respite is a luxury you can no longer afford."
			);
    	
		String text0 = "§kHerobrine";
        String text1 = "§r joined the game.";
        
        String text2 = "<§kHerobrine";
        String text3 = "§r> ";
        String text4 = chatMessages.get(rand.nextInt(chatMessages.size()));
        
        if(server != null)
        {
        	ScheduledExecutorService executorService1 = Executors.newSingleThreadScheduledExecutor();
            executorService1.schedule(() -> 
            { 
            	server.getPlayerManager().broadcast(Text.literal(text0 + text1).styled(style -> style.withColor(Formatting.YELLOW)), false); 
            	for (PlayerEntity player : world.getPlayers()) 
                {
                	BlockPos playerPos = player.getBlockPos();
                    BlockPos soundPos = playerPos.add(4, 0, 4);
                    world.playSound(null, soundPos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.AMBIENT, 1.0F, 1.0F);
                }
            }, 6, TimeUnit.SECONDS); 
            executorService1.shutdown();
        	
        	ScheduledExecutorService executorService2 = Executors.newSingleThreadScheduledExecutor();
            executorService2.schedule(() -> 
            { 
            	server.getPlayerManager().broadcast(Text.literal(text2 + text3 + text4).styled(style -> style.withColor(Formatting.WHITE)), false); 
            	for (PlayerEntity player : world.getPlayers()) 
                {
                	BlockPos playerPos = player.getBlockPos();
                    BlockPos soundPos = playerPos.add(4, 0, 4);
                    world.playSound(null, soundPos, SoundEvents.ENTITY_LIGHTNING_BOLT_THUNDER, SoundCategory.AMBIENT, 1.0F, 1.0F);
                }
            }, 12, TimeUnit.SECONDS); 
            executorService2.shutdown();
        }
        
        setAltarActive(world, true);
    }
    
    public static boolean hasHerobrineGreeted(ServerWorld world) 
    {
        try 
        {
            File f = world.getServer().getSavePath(WorldSavePath.ROOT).resolve(HIM_GREET_STATE).toFile();
            if (f.exists()) {
                Properties props = new Properties();
                try (InputStream in = new FileInputStream(f)) 
                {
                    props.load(in);
                    return Boolean.parseBoolean(props.getProperty("HasGreeted", "false"));
                }
            }
        } 
        catch (IOException e) 
        {
            Him.LOGGER.error("Could not load herobrine greeting!", e);
        }
        return false;
    }
    
    public static void setHerobrineGreeting(ServerWorld world, boolean greeting) 
    {
        try 
        {
            File f = world.getServer().getSavePath(WorldSavePath.ROOT).resolve(HIM_GREET_STATE).toFile();
            Properties props = new Properties();
            props.setProperty("HasGreeted", Boolean.toString(greeting));
            try (OutputStream out = new FileOutputStream(f)) 
            {
                props.store(out, "Herobrine Greet State");
            }
        } 
        catch (IOException e) 
        {
            Him.LOGGER.error("Could not save herobrine greeting!", e);
        }
    }
    
    public static boolean isAltarActive(ServerWorld world) 
    {    	            
        try 
        {
            File f = world.getServer().getSavePath(WorldSavePath.ROOT).resolve(HIM_ALTAR_STATE).toFile();
            if (f.exists()) {
                Properties props = new Properties();
                try (InputStream in = new FileInputStream(f)) 
                {
                    props.load(in);
                    return Boolean.parseBoolean(props.getProperty("AltarState", "false"));
                }
            }
        } 
        catch (IOException e) 
        {
        	Him.LOGGER.error("Could not load altar state!", e);
        }
        return false;
    }
    
    public static void setAltarActive(ServerWorld world, boolean active) 
    {
        try 
        {
            File f = world.getServer().getSavePath(WorldSavePath.ROOT).resolve(HIM_ALTAR_STATE).toFile();
            Properties props = new Properties();
            props.setProperty("AltarState", Boolean.toString(active));
            try (OutputStream out = new FileOutputStream(f)) 
            {
                props.store(out, "Herobrine Altar State");
            }
        } 
        catch (IOException e) 
        {
            Him.LOGGER.error("Could not save altar state!", e);
        }
    }

    public static void registerEventHandlers() 
    {
        UseBlockCallback.EVENT.register((player, world, hand, blockHitResult) -> 
        {
            BlockPos pos = blockHitResult.getBlockPos();

            if (!world.isClient())
            { 
                ServerWorld serverWorld = (ServerWorld) world;
                
                // CHECK IF ALTAR IS BUILT & INACTIVE - SUMMON
                if (!isAltarActive(serverWorld) && !Him.isHerobrineSeed && (player.getStackInHand(hand).getItem() == Items.FLINT_AND_STEEL || player.getStackInHand(hand).getItem() == Items.FIRE_CHARGE) && serverWorld.getBlockState(pos).getBlock() == Blocks.NETHERRACK && isAltarBuilt(pos, serverWorld))
                {
                    Him.LOGGER.info("You have no idea what you've done...");
                    summonHerobrine(pos, serverWorld, player);
                    serverWorld.playSound(null, pos, Him.ALTAR_SUMMONED, SoundCategory.AMBIENT, 0.4F, 1F);
                    return ActionResult.SUCCESS;
                }
                
                // CHECK IF ALTAR IS BUILT & ACTIVE - BANISH
                if (isAltarActive(serverWorld) && !Him.isHerobrineSeed && player.getStackInHand(hand).getItem() == Items.NETHER_STAR && serverWorld.getBlockState(pos).getBlock() == Blocks.NETHERRACK && isAltarBuilt(pos, serverWorld))
                {
                    Him.LOGGER.info("Sent his ass right back to Beta 1.6.6!");
                    banishHerobrine(pos, serverWorld, player);
                    serverWorld.playSound(null, pos, Him.ALTAR_BANISHED, SoundCategory.AMBIENT, 0.4F, 1F);
                    return ActionResult.SUCCESS;
                }
                
                // CHECK IF ALTAR IS BUILT & ACTIVE & WORLD IS CURSED - REVEAL PLAYER'S DEMISE
                else if (isAltarActive(serverWorld) && Him.isHerobrineSeed && player.getStackInHand(hand).getItem() == Items.NETHER_STAR && serverWorld.getBlockState(pos).getBlock() == Blocks.NETHERRACK && isAltarBuilt(pos, serverWorld)) 
                {
                	Random rand = new Random();
                	
                	List<String> chatMessages = Arrays.asList(
            				"No, I think not.",
            				"This is my domain.",
            				"You will find no salvation on this world.",
            				"You think you could just end it that easily? Here? No.",
            				"I'm afraid there is no escaping this nightmare.",
            				"You doomed this world the moment you stepped in it.",
            				"Try as you might, you cannot escape me.",
            				"What a horrible night to have a curse.",
            				"Now you will realize the futility of your efforts.",
            				"Your vain attempt has only brought you closer to the void.",
            				"Your tools will break, your armor will crumble, and you will perish.",
            				"My existence on this world is permanent, yours is not.",
            				"You really believed you could end it like this?",
            				"You can't erase what's etched in the very fabric of this world.",
            				"I'm afraid the fate of this world was sealed long ago.",
            				"I was here before you, I shall be here after.",
            				"You cannot silence me on this corrupted world.",
            				"This primitive shrine is a pathetic attempt to end me.",
            				"You cannot banish me from my own realm!"
            			);
                	
                	String text0 = "<§kHerobrine";
                    String text1 = "§r> ";
                    String text2 = chatMessages.get(rand.nextInt(chatMessages.size()));
                    
                    player.sendMessage(Text.literal(text0 + text1 + text2).styled(style -> style.withColor(Formatting.WHITE)), false);
                    
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 0));
                    player.addStatusEffect(new StatusEffectInstance(StatusEffects.BLINDNESS, 60, 0));
                    
                    if (!world.isClient()) 
                    {
                        LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
                        lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(pos));
                        world.spawnEntity(lightningEntity);
                        
                        ((ServerWorld) world).createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 10.0F, ServerWorld.ExplosionSourceType.TNT);
                        ((ServerWorld)world).setWeather(0, 12000, true, true);
                    }
                }
            }

            return ActionResult.PASS;
        });
    }

    public static boolean isAltarBuilt(BlockPos pos, ServerWorld world) 
    {
        int x = pos.getX();
        int y = pos.getY();
        int z = pos.getZ();

        // netherrack (origin point)
        if (world.getBlockState(new BlockPos(x, y, z)).isOf(Blocks.NETHERRACK)
        		// mossy cobblestone 
                && world.getBlockState(new BlockPos(x, y - 1, z)).isOf(Blocks.MOSSY_COBBLESTONE)
                
                // gold block sides
                && world.getBlockState(new BlockPos(x + 1, y - 1, z)).isOf(Blocks.GOLD_BLOCK)
                && world.getBlockState(new BlockPos(x - 1, y - 1, z)).isOf(Blocks.GOLD_BLOCK)
                && world.getBlockState(new BlockPos(x, y - 1, z + 1)).isOf(Blocks.GOLD_BLOCK)
                && world.getBlockState(new BlockPos(x, y - 1, z - 1)).isOf(Blocks.GOLD_BLOCK)
                
                // gold block corners
                && world.getBlockState(new BlockPos(x + 1, y - 1, z - 1)).isOf(Blocks.GOLD_BLOCK)
                && world.getBlockState(new BlockPos(x + 1, y - 1, z + 1)).isOf(Blocks.GOLD_BLOCK)
                && world.getBlockState(new BlockPos(x - 1, y - 1, z + 1)).isOf(Blocks.GOLD_BLOCK)
                && world.getBlockState(new BlockPos(x - 1, y - 1, z - 1)).isOf(Blocks.GOLD_BLOCK)
                
                // redstone torches
                && world.getBlockState(new BlockPos(x, y, z + 1)).isOf(Blocks.REDSTONE_TORCH)
                && world.getBlockState(new BlockPos(x, y, z - 1)).isOf(Blocks.REDSTONE_TORCH)
                && world.getBlockState(new BlockPos(x + 1, y, z)).isOf(Blocks.REDSTONE_TORCH)
                && world.getBlockState(new BlockPos(x - 1, y, z)).isOf(Blocks.REDSTONE_TORCH)
                
                // air corners
                && world.getBlockState(new BlockPos(x + 1, y, z + 1)).isAir()
                && world.getBlockState(new BlockPos(x - 1, y, z + 1)).isAir()
                && world.getBlockState(new BlockPos(x + 1, y, z - 1)).isAir()
                && world.getBlockState(new BlockPos(x - 1, y, z - 1)).isAir())
            return true;
        else
            return false;
    }

    public static void summonHerobrine(BlockPos pos, ServerWorld world, PlayerEntity player) 
    {
    	setAltarActive(world, true);
    	
    	String text1 = "§kHerobrine";
        String text2 = "§r joined the game.";
        
        player.sendMessage(Text.literal(text1 + text2).styled(style -> style.withColor(Formatting.YELLOW)), false);
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.WITHER, 100, 0));
        
        if (!world.isClient()) 
        {
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(pos));
            world.spawnEntity(lightningEntity);
            
            ((ServerWorld) world).createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 0, ServerWorld.ExplosionSourceType.TNT);
            world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
            
            ((ServerWorld)world).setWeather(0, 12000, true, true);
        }
    }
    
    public static void banishHerobrine(BlockPos pos, ServerWorld world, PlayerEntity player) 
    {
    	setAltarActive(world, false);
    	
    	String text1 = "§k Herobrine";
        String text2 = "§r was banished from the game.";
        
        player.sendMessage(Text.literal(text1 + text2).styled(style -> style.withColor(Formatting.YELLOW)), false);
        
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.LUCK, 12000, 0));
        player.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 300, 0));
        
        if (!world.isClient()) 
        {
            LightningEntity lightningEntity = EntityType.LIGHTNING_BOLT.create(world);
            lightningEntity.refreshPositionAfterTeleport(Vec3d.ofBottomCenter(pos));
            world.spawnEntity(lightningEntity);
            
            ((ServerWorld) world).createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 2, ServerWorld.ExplosionSourceType.BLOCK);
            ((ServerWorld) world).setWeather(12000, 0, false, false);
        }
    }
}