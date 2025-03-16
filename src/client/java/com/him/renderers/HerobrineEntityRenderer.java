package com.him.renderers;

import com.him.entities.HerobrineEntity;
import com.him.models.HerobrineModel;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;
import net.minecraft.util.Identifier;

public class HerobrineEntityRenderer extends MobEntityRenderer<HerobrineEntity, BipedEntityRenderState, HerobrineModel>
{
	private static final Identifier TEXTURE = Identifier.of("him", "textures/entity/herobrine.png");

	public HerobrineEntityRenderer(Context context, HerobrineModel model, float shadowRadius)
	{
		super(context, model, shadowRadius);
	}

	@Override
	public Identifier getTexture(BipedEntityRenderState state)
	{
		return TEXTURE;
	}

	@Override
	public BipedEntityRenderState createRenderState()
	{
		return new BipedEntityRenderState();
	}
}