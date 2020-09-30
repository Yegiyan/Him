package com.hikeyegiyan.him.world.structures;

import java.util.Random;
import java.util.function.Function;

import org.apache.logging.log4j.Level;

import com.hikeyegiyan.him.Him;
import com.hikeyegiyan.him.init.StructureInit;
import com.hikeyegiyan.him.util.HimData;
import com.mojang.datafixers.Dynamic;

import net.minecraft.util.Rotation;
import net.minecraft.util.SharedSeedRandom;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeManager;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class MossyCobbleHutStructure extends Structure<NoFeatureConfig>
{
	public MossyCobbleHutStructure(Function<Dynamic<?>, ? extends NoFeatureConfig> config)
	{
		super(config);
	}

	// Determines the valid chunks that this structure can spawn in.
	@Override
	protected ChunkPos getStartPositionForPosition(ChunkGenerator<?> chunkGenerator, Random random, int x, int z,
			int spacingOffsetsX, int spacingOffsetsZ)
	{
		// This means the structure cannot be closer than 7 chunks or more than 12 chunks
		int maxDistance = 32;
		int minDistance = 16;

		int xTemp = x + maxDistance * spacingOffsetsX;
		int ztemp = z + maxDistance * spacingOffsetsZ;
		int xTemp2 = xTemp < 0 ? xTemp - maxDistance + 1 : xTemp;
		int zTemp2 = ztemp < 0 ? ztemp - maxDistance + 1 : ztemp;
		int validChunkX = xTemp2 / maxDistance;
		int validChunkZ = zTemp2 / maxDistance;

		((SharedSeedRandom) random).setLargeFeatureSeedWithSalt(chunkGenerator.getSeed(), validChunkX, validChunkZ,
				this.getSeedModifier());
		validChunkX = validChunkX * maxDistance;
		validChunkZ = validChunkZ * maxDistance;
		validChunkX = validChunkX + random.nextInt(maxDistance - minDistance);
		validChunkZ = validChunkZ + random.nextInt(maxDistance - minDistance);

		return new ChunkPos(validChunkX, validChunkZ);
	}

	// The structure name to show in the /locate command.
	@Override
	public String getStructureName()
	{
		return Him.MODID + ":mossy_cobble_hut";
	}

	
	// Unused but cannot be removed, just return 0.
	@Override
	public int getSize()
	{
		return 0;
	}

	// This is how the worldgen code will start the generation of our structure when it passes the checks.
	@Override
	public Structure.IStartFactory getStartFactory()
	{
		return MossyCobbleHutStructure.Start::new;
	}

	/*
	 * This is used so that if two structure's has the same spawn location
	 * algorithm, they will not end up in perfect sync as long as they have
	 * different seed modifier.
	 * 
	 * So make this a big random number that is unique only to this structure.
	 */
	protected int getSeedModifier()
	{
		return 1111111111;
	}
	
	public static void determineBiome()
	{
		for (Biome biome : ForgeRegistries.BIOMES)
		{	
			biome.addFeature(GenerationStage.Decoration.SURFACE_STRUCTURES, StructureInit.MOSSY_COBBLE_HUT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG).
					withPlacement(Placement.NOPE.configure(IPlacementConfig.NO_PLACEMENT_CONFIG)));
			
			// Biome conditions for a structure to spawn
			// You can even filter by temperature, scale, etc.
			if (biome == Biomes.SNOWY_TUNDRA ||
				biome == Biomes.SNOWY_TAIGA ||
				biome == Biomes.GIANT_TREE_TAIGA ||
				biome == Biomes.GIANT_SPRUCE_TAIGA ||
				biome == Biomes.PLAINS ||
				biome == Biomes.SUNFLOWER_PLAINS ||
				biome == Biomes.FOREST ||
				biome == Biomes.FLOWER_FOREST ||
				biome == Biomes.BIRCH_FOREST ||
				biome == Biomes.SWAMP ||
				biome == Biomes.JUNGLE ||
				biome == Biomes.SAVANNA ||
				biome == Biomes.DESERT ||
				biome == Biomes.BADLANDS)
			{			
				if (HimData.get(null).getAltarActive())
				{
					Him.LOGGER.info("determineBiome() Called!");
					biome.addStructure(StructureInit.MOSSY_COBBLE_HUT.withConfiguration(IFeatureConfig.NO_FEATURE_CONFIG));
				}
			}
		}
	}

	/*
	 * This is where all the checks will be done to determine if the structure can
	 * spawn here.
	 * 
	 * Basically, this method is used for determining if the chunk coordinates are
	 * valid, if certain other structures are too close or not, or some other
	 * restrictive condition.
	 *
	 * For example, Pillager Outposts added a check to make sure it cannot spawn
	 * within 10 chunk of a Village.
	 */

	@Override
	public boolean canBeGenerated(BiomeManager p_225558_1_, ChunkGenerator<?> chunkGen, Random rand, int chunkPosX,
			int chunkPosZ, Biome biome)
	{
		ChunkPos chunkpos = this.getStartPositionForPosition(chunkGen, rand, chunkPosX, chunkPosZ, 0, 0);

		// Checks to see if current chunk is valid to spawn in.
		if (chunkPosX == chunkpos.x && chunkPosZ == chunkpos.z)
		{
			// Checks if the biome can spawn this structure.
			if (chunkGen.hasStructure(biome, this))
			{
				return true;
			}
		}

		return false;
	}

	// Handles calling up the structure's pieces class and height that structure will spawn at.
	public static class Start extends StructureStart
	{
		public Start(Structure<?> structureIn, int chunkX, int chunkZ, MutableBoundingBox mutableBoundingBox,
				int referenceIn, long seedIn)
		{
			super(structureIn, chunkX, chunkZ, mutableBoundingBox, referenceIn, seedIn);
		}

		@Override
		public void init(ChunkGenerator<?> generator, TemplateManager templateManagerIn, int chunkX, int chunkZ,
				Biome biomeIn)
		{
			// Check out vanilla's WoodlandMansionStructure for how they offset the x and z
			// so that they get the y value of the land at the mansion's entrance, no matter
			// which direction the mansion is rotated. However, for most purposes, getting the 
			// y value of land with the default x and z is good enough.
			Rotation rotation = Rotation.values()[this.rand.nextInt(Rotation.values().length)];

			// Turns the chunk coordinates into actual coordinates we can use. (Gets center of that chunk)
			int x = (chunkX << 4) + 7;
			int z = (chunkZ << 4) + 7;

			// Finds the y value of the terrain at location.
			int surfaceY = generator.getHeight(x, z, Heightmap.Type.WORLD_SURFACE_WG);
			BlockPos blockpos = new BlockPos(x, surfaceY, z);

			// Now adds the structure pieces to this.components with all details such as
			// where each part goes so that the structure can be added to the world by worldgen.
			MossyCobbleHutPieces.start(templateManagerIn, blockpos, rotation, this.components, this.rand);

			// Sets the bounds of the structure.
			this.recalculateStructureSize();

			// A debug LOGGER to quickly find out if the structure is spawning or not and where it is.
			Him.LOGGER.log(Level.DEBUG, "Mossy Cobble Hut " + (blockpos.getX()) + " " + blockpos.getY() + " " + (blockpos.getZ()));
		}

	}
}
