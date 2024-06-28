package com.him.renderers;

import com.him.entities.HerobrineEntity;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.render.entity.model.BipedEntityModel;
import net.minecraft.client.render.entity.model.EntityModelLayers;
import net.minecraft.util.Identifier;

public class HerobrineEntityRenderer extends MobEntityRenderer<HerobrineEntity, BipedEntityModel<HerobrineEntity>> 
{
    private static final Identifier TEXTURE = Identifier.of("him", "textures/entity/herobrine.png");

    public HerobrineEntityRenderer(EntityRendererFactory.Context context) 
    {
        super(context, new BipedEntityModel<>(context.getPart(EntityModelLayers.PLAYER)), 0.5F);
    }

    @Override
    public Identifier getTexture(HerobrineEntity entity) 
    {
        return TEXTURE;
    }
}