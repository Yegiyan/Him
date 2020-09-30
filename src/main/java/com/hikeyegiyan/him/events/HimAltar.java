package com.hikeyegiyan.him.events;

import com.hikeyegiyan.him.Him;
import com.hikeyegiyan.him.init.SoundInit;
import com.hikeyegiyan.him.util.HimData;

import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = Him.MODID, bus = Bus.FORGE)
public class HimAltar
{
	@SubscribeEvent
	public static void activateAltar(PlayerInteractEvent.RightClickBlock event)
	{
		World world = event.getWorld();
		BlockPos pos = event.getPos();

		PlayerEntity player = event.getPlayer();
		Hand hand = event.getHand();
		ItemStack itemStack = player.getHeldItem(hand);
		
		//Him.LOGGER.info("Is Herobrine Spawned: " + HimData.get(world).getAltarActive());
		
		// Summon Him
		if ((isAltarBuilt(pos, world)) && !HimData.get(world).getAltarActive() && (itemStack.getItem() == Items.FLINT_AND_STEEL))
		{
			if (world instanceof ServerWorld)
			{
				player.addPotionEffect(new EffectInstance(Effects.WITHER, 100, 0));
				player.addPotionEffect(new EffectInstance(Effects.BLINDNESS, 20, 0));
				
				String text1 = TextFormatting.YELLOW + "§k Herobrine ";
				String text2 = TextFormatting.YELLOW + "joined the game.";
				player.sendMessage(new StringTextComponent(text1 + text2));
				
				((ServerWorld) world).addLightningBolt(new LightningBoltEntity(world, pos.getX(), pos.getY(), pos.getZ(), false));
				((ServerWorld) world).getWorldInfo().setClearWeatherTime(0);
				((ServerWorld) world).getWorldInfo().setThundering(true);
				((ServerWorld) world).getWorldInfo().setRaining(true);
				((ServerWorld) world).getWorldInfo().setThunderTime(12000); // 20*(60*10)
				((ServerWorld) world).getWorldInfo().setRainTime(12000);
				
				world.playSound(null, new BlockPos(player), SoundInit.SUMMONED_SOUND.get(), SoundCategory.AMBIENT, 1.0f, 1.0f);
				HimData.get(world).setAltarActive(true);
			}
		}
		
		// Banish Him
		if ((isAltarBuilt(pos, world)) && HimData.get(world).getAltarActive() && (itemStack.getItem() == Items.NETHER_STAR))
		{
			if (world instanceof ServerWorld)
			{	
				player.addPotionEffect(new EffectInstance(Effects.LUCK, 12000, 0));
				
				String text1 = TextFormatting.YELLOW + "§k Herobrine ";
				String text2 = TextFormatting.YELLOW + "was banished from the game.";
				player.sendMessage(new StringTextComponent(text1 + text2));
				
				((ServerWorld) world).addLightningBolt(new LightningBoltEntity(world, pos.getX(), pos.getY(), pos.getZ(), false));
				((ServerWorld) world).createExplosion(null, pos.getX(), pos.getY(), pos.getZ(), 8, null);
				((ServerWorld) world).getWorldInfo().setClearWeatherTime(10000);
				((ServerWorld) world).getWorldInfo().setDayTime(1000);
				
				world.playSound(null, new BlockPos(player), SoundInit.BANISHED_SOUND.get(), SoundCategory.AMBIENT, 0.8f, 1.0f);
				HimData.get(world).setAltarActive(false);
			}
		}
	}
	
	public static boolean isAltarBuilt(BlockPos pos, World world)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();

		// Netherrack is the origin point
		if (world.getBlockState(new BlockPos(x, y, z)) == Blocks.NETHERRACK.getDefaultState()
				&& world.getBlockState(new BlockPos(x, y - 1, z)) == Blocks.MOSSY_COBBLESTONE.getDefaultState()
				// Gold Block Sides
				&& world.getBlockState(new BlockPos(x + 1, y - 1, z)) == Blocks.GOLD_BLOCK.getDefaultState()
				&& world.getBlockState(new BlockPos(x - 1, y - 1, z)) == Blocks.GOLD_BLOCK.getDefaultState()
				&& world.getBlockState(new BlockPos(x, y - 1, z + 1)) == Blocks.GOLD_BLOCK.getDefaultState()
				&& world.getBlockState(new BlockPos(x, y - 1, z - 1)) == Blocks.GOLD_BLOCK.getDefaultState()
				// Gold Block Corners
				&& world.getBlockState(new BlockPos(x + 1, y - 1, z - 1)) == Blocks.GOLD_BLOCK.getDefaultState()
				&& world.getBlockState(new BlockPos(x + 1, y - 1, z + 1)) == Blocks.GOLD_BLOCK.getDefaultState()
				&& world.getBlockState(new BlockPos(x - 1, y - 1, z + 1)) == Blocks.GOLD_BLOCK.getDefaultState()
				&& world.getBlockState(new BlockPos(x - 1, y - 1, z - 1)) == Blocks.GOLD_BLOCK.getDefaultState()
				// Redstone Torches
				&& world.getBlockState(new BlockPos(x, y, z + 1)) == Blocks.REDSTONE_TORCH.getDefaultState()
				&& world.getBlockState(new BlockPos(x, y, z - 1)) == Blocks.REDSTONE_TORCH.getDefaultState()
				&& world.getBlockState(new BlockPos(x + 1, y, z)) == Blocks.REDSTONE_TORCH.getDefaultState()
				&& world.getBlockState(new BlockPos(x - 1, y, z)) == Blocks.REDSTONE_TORCH.getDefaultState()
				// Air Corners
				&& world.getBlockState(new BlockPos(x + 1, y, z + 1)) == Blocks.AIR.getDefaultState()
				&& world.getBlockState(new BlockPos(x - 1, y, z + 1)) == Blocks.AIR.getDefaultState()
				&& world.getBlockState(new BlockPos(x + 1, y, z - 1)) == Blocks.AIR.getDefaultState()
				&& world.getBlockState(new BlockPos(x - 1, y, z - 1)) == Blocks.AIR.getDefaultState())
			return true;
		else
			return false;
	}
}
