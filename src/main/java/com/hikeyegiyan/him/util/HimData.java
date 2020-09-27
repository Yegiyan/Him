package com.hikeyegiyan.him.util;

import java.util.Objects;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.storage.WorldSavedData;

public class HimData extends WorldSavedData
{
	public static final String DATA_ID = "him";
	static HimData clientSide = new HimData();
	public boolean isAltarActive = false;

	public HimData()
	{
		super(DATA_ID);
	}

	@Override
	public void read(CompoundNBT nbt)
	{
		isAltarActive = nbt.getBoolean("isAltarActive");
	}

	@Override
	public CompoundNBT write(CompoundNBT nbt)
	{
		nbt.putBoolean("isAltarActive", isAltarActive);
		return nbt;
	}

	public void setAltarActive(boolean bool)
	{
		isAltarActive = bool;
	}
	
	public boolean getAltarActive()
	{
		markDirty();
		return isAltarActive;
	}

	public static HimData get(World world)
	{
		if (world instanceof ServerWorld)
		{
			return Objects.requireNonNull(world.getServer()).getWorld(DimensionType.OVERWORLD).getSavedData()
					.getOrCreate(HimData::new, DATA_ID);
		}
		else
			return clientSide;
	}
}
