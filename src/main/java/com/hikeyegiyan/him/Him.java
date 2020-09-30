package com.hikeyegiyan.him;

import java.util.Locale;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.hikeyegiyan.him.init.ItemInit;
import com.hikeyegiyan.him.init.SoundInit;
import com.hikeyegiyan.him.init.StructureInit;

import net.minecraft.util.registry.Registry;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Him.MODID)
public class Him
{
	// Directly reference a log4j logger.
	public static final Logger LOGGER = LogManager.getLogger();
	public static final String MODID = "him";

	public Him()
	{
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);

		modEventBus.addListener(this::setup);

		SoundInit.SOUNDS.register(modEventBus);
		ItemInit.ITEMS.register(modEventBus);

		// Register ourselves for server and other game events we are interested in
		MinecraftForge.EVENT_BUS.register(this);
	}

	private void setup(final FMLCommonSetupEvent event)
	{
		StructureInit.addToBiomes();
	}

	// Registers the structures pieces themselves. If you don't do this part, Forge
	// will complain to you in the Console.
	static IStructurePieceType register(IStructurePieceType structurePiece, String key)
	{
		return Registry.register(Registry.STRUCTURE_PIECE, key.toLowerCase(Locale.ROOT), structurePiece);
	}
}
