package com.hikeyegiyan.him.init;

import com.hikeyegiyan.him.world.structures.MossyCobbleHutPieces;
import com.hikeyegiyan.him.world.structures.MossyCobbleHutStructure;

import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.IStructurePieceType;
import net.minecraft.world.gen.feature.structure.Structure;

public class StructureInit
{
	// Static instance of our structure so we can reference it and add it to biomes easily.
		public static Structure<NoFeatureConfig> MOSSY_COBBLE_HUT = new MossyCobbleHutStructure(NoFeatureConfig::deserialize);
		public static IStructurePieceType MCHP = MossyCobbleHutPieces.Piece::new;
		
		public static void addToBiomes()
		{
			MossyCobbleHutStructure.determineBiome();
		}
}
