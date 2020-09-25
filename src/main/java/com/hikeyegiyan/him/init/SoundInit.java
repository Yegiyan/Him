package com.hikeyegiyan.him.init;

import com.hikeyegiyan.him.Him;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class SoundInit
{
	@SuppressWarnings("deprecation")
	public static final DeferredRegister<SoundEvent> SOUNDS = new DeferredRegister<>(ForgeRegistries.SOUND_EVENTS, Him.MODID);
	
	public static final RegistryObject<SoundEvent> SUMMONED_SOUND = SOUNDS.register("altar_summoned", () -> new SoundEvent(new ResourceLocation(Him.MODID, "altar_summoned")));
	public static final RegistryObject<SoundEvent> SIGHTING_SOUND = SOUNDS.register("sighting", () -> new SoundEvent(new ResourceLocation(Him.MODID, "sighting")));
	public static final RegistryObject<SoundEvent> BANISHED_SOUND = SOUNDS.register("altar_destroyed", () -> new SoundEvent(new ResourceLocation(Him.MODID, "altar_destroyed")));
}
