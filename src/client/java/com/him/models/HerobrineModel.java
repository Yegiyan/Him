package com.him.models;

import net.minecraft.client.render.entity.EntityRendererFactory.Context;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.client.render.entity.state.BipedEntityRenderState;

public class HerobrineModel extends BipedEntityModel<BipedEntityRenderState>
{
    public HerobrineModel(Context context)
    {
        super(context.getPart(EntityModelLayers.PLAYER));
    }
}