package com.hikeyegiyan.him.world.structures;

import java.util.List;
import java.util.Map;
import java.util.Random;

import com.google.common.collect.ImmutableMap;
import com.hikeyegiyan.him.Him;
import com.hikeyegiyan.him.init.FeatureInit;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.world.IWorld;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.structure.StructurePiece;
import net.minecraft.world.gen.feature.structure.TemplateStructurePiece;
import net.minecraft.world.gen.feature.template.PlacementSettings;
import net.minecraft.world.gen.feature.template.Template;
import net.minecraft.world.gen.feature.template.TemplateManager;

public class MossyCobbleHutPieces
{
	private static final ResourceLocation MOSSY_COBBLE_HUT = new ResourceLocation(Him.MODID + ":mossy_cobble_hut");
	private static final Map<ResourceLocation, BlockPos> OFFSET = ImmutableMap.of(MOSSY_COBBLE_HUT, new BlockPos(0, 0, 0));

	// Begins assembling your structure and where the pieces needs to go.
	public static void start(TemplateManager templateManager, BlockPos pos, Rotation rotation,
			List<StructurePiece> pieceList, Random random)
	{
		int x = pos.getX();
		int z = pos.getZ();

		// This is how we factor in rotation for multi-piece structures.
		// I would recommend using the OFFSET map above to have each piece at correct
		// height relative of each other and keep the X and Z equal to 0.
		BlockPos rotationOffSet = new BlockPos(0, -1, 0).rotate(rotation);
		BlockPos blockpos = rotationOffSet.add(x, pos.getY(), z);
		pieceList.add(new MossyCobbleHutPieces.Piece(templateManager, MOSSY_COBBLE_HUT, blockpos, rotation));
	}
	
	// Most of this doesn't need to be touched but you do have to pass in 
	// the IStructurePieceType you registered into the super constructors.
	public static class Piece extends TemplateStructurePiece
	{
		private ResourceLocation resourceLocation;
		private Rotation rotation;

		public Piece(TemplateManager templateManagerIn, ResourceLocation resourceLocationIn, BlockPos pos,
				Rotation rotationIn)
		{
			super(FeatureInit.MCHP, 0);
			this.resourceLocation = resourceLocationIn;
			BlockPos blockpos = MossyCobbleHutPieces.OFFSET.get(resourceLocation);
			this.templatePosition = pos.add(blockpos.getX(), blockpos.getY(), blockpos.getZ());
			this.rotation = rotationIn;
			this.setupPiece(templateManagerIn);
		}

		public Piece(TemplateManager templateManagerIn, CompoundNBT tagCompound)
		{
			super(FeatureInit.MCHP, tagCompound);
			this.resourceLocation = new ResourceLocation(tagCompound.getString("Template"));
			this.rotation = Rotation.valueOf(tagCompound.getString("Rot"));
			this.setupPiece(templateManagerIn);
		}

		private void setupPiece(TemplateManager templateManager)
		{
			Template template = templateManager.getTemplateDefaulted(this.resourceLocation);
			PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation)
					.setMirror(Mirror.NONE);
			this.setup(template, this.templatePosition, placementsettings);
		}

		// Helper method to read subclass data from NBT
		@Override
		protected void readAdditional(CompoundNBT tagCompound)
		{
			super.readAdditional(tagCompound);
			tagCompound.putString("Template", this.resourceLocation.toString());
			tagCompound.putString("Rot", this.rotation.name());
		}
		
		@Override
		protected void handleDataMarker(String function, BlockPos pos, IWorld world, Random rand,
				MutableBoundingBox sbb)
		{
			if ("chest".equals(function))
			{
				world.setBlockState(pos, Blocks.AIR.getDefaultState(), 3);
				TileEntity tileEntity = world.getTileEntity(pos.down());

				if (tileEntity instanceof ChestTileEntity)
				{
					// For Minecraft's Loot Tables:
					// https://mcreator.net/wiki/minecraft-vanilla-loot-tables-list
					
					//ResourceLocation customLootTable = new ResourceLocation(Him.MODID, "chests/loot_table_name"); (JSON file)
					ResourceLocation minecraftLootTable = new ResourceLocation("minecraft:chests/simple_dungeon");
					((ChestTileEntity) tileEntity).setLootTable(minecraftLootTable, rand.nextLong());

				}
			}
		}

		@Override
		public boolean create(IWorld worldIn, ChunkGenerator<?> p_225577_2_, Random randomIn,
				MutableBoundingBox structureBoundingBoxIn, ChunkPos chunkPos)
		{
			PlacementSettings placementsettings = (new PlacementSettings()).setRotation(this.rotation)
					.setMirror(Mirror.NONE);
			BlockPos blockpos = MossyCobbleHutPieces.OFFSET.get(this.resourceLocation);
			this.templatePosition.add(Template.transformedBlockPos(placementsettings,
					new BlockPos(0 - blockpos.getX(), 0, 0 - blockpos.getZ())));

			return super.create(worldIn, p_225577_2_, randomIn, structureBoundingBoxIn, chunkPos);
		}
	}

}
