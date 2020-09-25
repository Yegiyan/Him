package com.hikeyegiyan.him.util;

import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class HimItemGroup extends ItemGroup
{
	public static final ItemGroup HIM_TAB = new HimItemGroup("him_group");

	public HimItemGroup(String name)
	{
		super(name);
	}

	@Override
	public ItemStack createIcon()
	{
		return new ItemStack(Items.BELL);
	}
}
