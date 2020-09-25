package com.hikeyegiyan.him.init;

import com.hikeyegiyan.him.Him;
import com.hikeyegiyan.him.items.TheInstarEmergenceDisc;
import com.hikeyegiyan.him.util.HimItemGroup;

import net.minecraft.item.Item;
import net.minecraft.item.Rarity;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ItemInit
{
	@SuppressWarnings("deprecation")
	public static final DeferredRegister<Item> ITEMS = new DeferredRegister<Item>(ForgeRegistries.ITEMS, Him.MODID);

	public static final RegistryObject<Item> THE_INSTAR_EMERGENCE_MUSIC_DISC = ITEMS.register(
			"the_instar_emergence_disc",
			() -> new TheInstarEmergenceDisc(1, SoundInit.THE_INSTAR_EMERGENCE_DISC_lAZY.get(),
					new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(HimItemGroup.HIM_TAB)));
	
	public static final RegistryObject<Item> INTERCONNECTEDNESS_MUSIC_DISC = ITEMS.register(
			"interconnectedness_disc",
			() -> new TheInstarEmergenceDisc(1, SoundInit.INTERCONNECTEDNESS_DISC_LAZY.get(),
					new Item.Properties().maxStackSize(1).rarity(Rarity.RARE).group(HimItemGroup.HIM_TAB)));
}
