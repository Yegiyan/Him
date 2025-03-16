package com.him;

import com.him.models.HerobrineModel;
import com.him.renderers.HerobrineEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class HimClient implements ClientModInitializer 
{
	@Override
	public void onInitializeClient() 
	{
		EntityRendererRegistry.register(Him.HEROBRINE, (context) -> { HerobrineModel model = new HerobrineModel(context); return new HerobrineEntityRenderer(context, model, 0.5F); });
	}
}