package com.hikeyegiyan.him.init;

import com.hikeyegiyan.him.Him;
import com.hikeyegiyan.him.util.HimItemGroup;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents
{
	public static final ItemGroup HIM = HimItemGroup.HIM_TAB;
	
	@SubscribeEvent
	public static void registerItems(final RegistryEvent.Register<Item> event)
	{
		
	}
	
	@SubscribeEvent
	public static void onRegisterStructures(final RegistryEvent.Register<Feature<?>> event)
	{
		IForgeRegistry<Feature<?>> registry = event.getRegistry();
		
		register(registry, StructureInit.MOSSY_COBBLE_HUT, "mossy_cobble_hut");
        Registry.register(Registry.STRUCTURE_PIECE, "mossy_cobble_hut", StructureInit.MCHP);
	}
	
	public static ResourceLocation location(String name) 
	{
		return new ResourceLocation(Him.MODID, name);
	}
	
	// Helper method to quickly register features, blocks, items, structures, biomes, anything that can be registered.
	public static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T entry, String registryKey)
	{
		entry.setRegistryName(new ResourceLocation(Him.MODID, registryKey));
		registry.register(entry);
		return entry;
	}
}
