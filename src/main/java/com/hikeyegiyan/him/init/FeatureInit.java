package com.hikeyegiyan.him.init;

import com.hikeyegiyan.him.Him;
import com.hikeyegiyan.him.world.structures.MossyCobbleHutPieces;
import com.hikeyegiyan.him.world.structures.MossyCobbleHutStructure;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class FeatureInit
{
	// Static instance of our structure so we can reference it and add it to biomes easily.
	public static Structure<NoFeatureConfig> MOSSY_COBBLE_HUT = new MossyCobbleHutStructure(NoFeatureConfig::deserialize);
	public static IStructurePieceType MCHP = MossyCobbleHutPieces.Piece::new;

	// Registers the structure itself and sets what its path is. It is always a good idea 
    // to register your regular features too so that other mods can use them too.
	@SubscribeEvent
	public static void onRegisterFeatures(final RegistryEvent.Register<Feature<?>> event)
	{
		IForgeRegistry<Feature<?>> registry = event.getRegistry();
		
		register(registry, MOSSY_COBBLE_HUT, "mossy_cobble_hut");
        Registry.register(Registry.STRUCTURE_PIECE, "mossy_cobble_hut", MCHP);
	}

	public static void addToBiomes()
	{
		MossyCobbleHutStructure.determineBiome();
	}
	
	// Helper method to quickly register features, blocks, items, structures, biomes, anything that can be registered.
	public static <T extends IForgeRegistryEntry<T>> T register(IForgeRegistry<T> registry, T entry, String registryKey)
	{
		entry.setRegistryName(new ResourceLocation(Him.MODID, registryKey));
		registry.register(entry);
		return entry;
	}
}
