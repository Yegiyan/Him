package com.him;

import com.him.renderers.HerobrineEntityRenderer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class HimClient implements ClientModInitializer 
{
	@Override
	public void onInitializeClient() 
	{
		// This entrypoint is suitable for setting up client-specific logic, such as rendering.
		EntityRendererRegistry.register(Him.HEROBRINE, (context) -> { return new HerobrineEntityRenderer(context); });
	}
}