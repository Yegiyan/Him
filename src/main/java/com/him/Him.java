package com.him;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.him.entities.HerobrineEntity;
import com.him.events.Altar;
import com.him.events.Grief;
import com.him.events.Haunt;
import com.him.events.Stalk;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricDefaultAttributeRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.entity.SpawnRestriction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.world.Heightmap;

public class Him implements ModInitializer
{
	public static String MOD_ID = "him";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	public static HimConfig CONFIG = HimConfig.loadConfig();
	
	public static final EntityType<HerobrineEntity> HEROBRINE = Registry.register(Registries.ENTITY_TYPE, Identifier.of("him", "herobrine"),
			EntityType.Builder.create(HerobrineEntity::new, SpawnGroup.CREATURE).dimensions(0.6F, 1.95F).build(RegistryKey.of(RegistryKeys.ENTITY_TYPE, Identifier.of("him", "herobrine"))));
	
	public static final SoundEvent ALTAR_SUMMONED = registerSoundEvent("altar_summoned");
    public static final SoundEvent ALTAR_BANISHED = registerSoundEvent("altar_banished");

    // music discs not implemented yet
    public static final SoundEvent THE_INSTAR_EMERGENCE_SOUND = registerSoundEvent("the_instar_emergence_disc");
    public static final SoundEvent INTERCONNECTEDNESS_SOUND = registerSoundEvent("interconnectedness_disc");
    
    public static final SoundEvent SIGHTING = registerSoundEvent("sighting");
    
    public static boolean isHerobrineSeed = false;
    
	@Override
	public void onInitialize() 
	{
		Altar.registerEventHandlers();
		
		FabricDefaultAttributeRegistry.register(HEROBRINE, HerobrineEntity.createMobAttributes());
		SpawnRestriction.register(Him.HEROBRINE, SpawnRestriction.getLocation(Him.HEROBRINE), Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, HerobrineEntity::canMobSpawn);
		
		// check if the world seed is cursed
        ServerLifecycleEvents.SERVER_STARTED.register(server -> 
        { 
        	long seed = server.getOverworld().getSeed();
        	isHerobrineSeed = seed == 478868574082066804L;
        });
		
        updateHim();
	}
	
	private void updateHim()
	{
	    Random rand = new Random();

	    int minSecondsStalk = CONFIG.getMinSecondsStalk();
	    int maxSecondsStalk = CONFIG.getMaxSecondsStalk();
	    
	    int minSecondsHaunt = CONFIG.getMinSecondsHaunt();
	    int maxSecondsHaunt = CONFIG.getMaxSecondsHaunt();
	    
	    int minSecondsGrief = CONFIG.getMinSecondsGrief();
	    int maxSecondsGrief = CONFIG.getMaxSecondsGrief();
	    
	    MutableInt secondsStalk = new MutableInt(rand.nextInt((maxSecondsStalk - minSecondsStalk) + 1) + minSecondsStalk);
	    MutableInt secondsHaunt = new MutableInt(rand.nextInt((maxSecondsHaunt - minSecondsHaunt) + 1) + minSecondsHaunt);
	    MutableInt secondsGrief = new MutableInt(rand.nextInt((maxSecondsGrief - minSecondsGrief) + 1) + minSecondsGrief);

	    AtomicInteger tickCounterStalk = new AtomicInteger();
	    AtomicInteger tickCounterHaunt = new AtomicInteger();
	    AtomicInteger tickCounterGrief = new AtomicInteger();
	    
	    AtomicInteger printCounter = new AtomicInteger();
	    boolean printDebugInfo = true;
	    int debugPrintInterval = 5;

	    ServerTickEvents.END_WORLD_TICK.register(world -> 
	    {
	        Haunt.isPlayerSleeping(world);

	        if (isHerobrineSeed && !Altar.hasHerobrineGreeted(world))
	        {
	        	Altar.setHerobrineGreeting(world, true);
	        	Altar.curseWorld(world);
	        	
	        	secondsStalk.setValue(15);
    		    secondsHaunt.setValue(30);
    		    secondsGrief.setValue(45);
	        }
	        
	        if (!world.getPlayers().isEmpty() && Altar.isAltarActive(world)) 
            {
	        	if ((printCounter.incrementAndGet() >= 20 * debugPrintInterval) && printDebugInfo)
		        {
	        		LOGGER.info("--------------");
		        	LOGGER.info("Time until next stalking: " + ((int)((secondsStalk.getValue() * 20 - tickCounterStalk.get()) / 20.0 + 0.5)) + " seconds");
		        	LOGGER.info("Time until next haunting: " + ((int)((secondsHaunt.getValue() * 20 - tickCounterHaunt.get()) / 20.0 + 0.5)) + " seconds");
		        	LOGGER.info("Time until next griefing: " + ((int)((secondsGrief.getValue() * 20 - tickCounterGrief.get()) / 20.0 + 0.5)) + " seconds");
			        printCounter.set(0);
		        }
		        
		        if(tickCounterStalk.incrementAndGet() >= 20 * secondsStalk.getValue()) 
		        {
		            tickCounterStalk.set(0);
		            secondsStalk.setValue(rand.nextInt((maxSecondsStalk - minSecondsStalk) + 1) + minSecondsStalk);
		            Stalk.stalk(world);
		        }

		        if(tickCounterHaunt.incrementAndGet() >= 20 * secondsHaunt.getValue()) 
		        {
		            tickCounterHaunt.set(0);
		            secondsHaunt.setValue(rand.nextInt((maxSecondsHaunt - minSecondsHaunt) + 1) + minSecondsHaunt);
		            Haunt.haunt(world);
		        }

		        if(tickCounterGrief.incrementAndGet() >= 20 * secondsGrief.getValue()) 
		        {
		            tickCounterGrief.set(0);
		            secondsGrief.setValue(rand.nextInt((maxSecondsGrief - minSecondsGrief) + 1) + minSecondsGrief);
		            Grief.grief(world);
		        }
            }
	    });
	}
    
    private static SoundEvent registerSoundEvent(String name)
	{
		Identifier id = Identifier.of(MOD_ID, name);
		return Registry.register(Registries.SOUND_EVENT, id, SoundEvent.of(id));
	}
}

class MutableInt 
{
    private int value;

    public MutableInt(int value) {
        this.value = value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}