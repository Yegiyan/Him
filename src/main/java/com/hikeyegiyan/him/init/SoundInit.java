package com.hikeyegiyan.him.init;

import com.hikeyegiyan.him.Him;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundInit
{
	@SuppressWarnings("deprecation")
	public static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, Him.MODID);
	
	// Altar Sounds
	public static final RegistryObject<SoundEvent> SUMMONED_SOUND = SOUNDS.register("altar_summoned", () -> new SoundEvent(new ResourceLocation(Him.MODID, "altar_summoned")));
	public static final RegistryObject<SoundEvent> BANISHED_SOUND = SOUNDS.register("altar_destroyed", () -> new SoundEvent(new ResourceLocation(Him.MODID, "altar_destroyed")));
	
	// Sighting Sounds
	public static final RegistryObject<SoundEvent> SIGHTING_SOUND = SOUNDS.register("sighting", () -> new SoundEvent(new ResourceLocation(Him.MODID, "sighting")));
	
	// Music Disc Sounds
	public static final Lazy<SoundEvent> THE_INSTAR_EMERGENCE_DISC_lAZY = Lazy.of(() -> new SoundEvent(new ResourceLocation(Him.MODID, "the_instar_emergence_disc")));
	public static final RegistryObject<SoundEvent> THE_INSTAR_EMERGENCE_DISC = SOUNDS.register("the_instar_emergence_disc", THE_INSTAR_EMERGENCE_DISC_lAZY);
	
	public static final Lazy<SoundEvent> INTERCONNECTEDNESS_DISC_LAZY = Lazy.of(() -> new SoundEvent(new ResourceLocation(Him.MODID, "interconnectedness_disc")));
	public static final RegistryObject<SoundEvent> INTERCONNECTEDNESS_DISC = SOUNDS.register("interconnectedness_disc", INTERCONNECTEDNESS_DISC_LAZY);
}
