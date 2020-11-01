package com.integral.lizardification.client.renderers;

import com.google.common.collect.ImmutableList;
import com.integral.lizardification.Lizardification;
import com.integral.lizardification.handlers.ClientConfigHandler;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.AgeableModel;
import net.minecraft.client.renderer.entity.model.EntityModel;
import net.minecraft.client.renderer.model.ModelRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class LizardLayer<T extends LivingEntity, M extends EntityModel<T>> extends LayerRenderer<T, M> {
	private PlayerModel<T> model;

	public LizardLayer(final IEntityRenderer<T, M> entityRendererIn) {
		super(entityRendererIn);
		this.model = new PlayerModel<T>();
	}

	@Override
	public void render(final MatrixStack matrixStackIn, final IRenderTypeBuffer bufferIn, final int packedLightIn, final T entitylivingbaseIn, final float limbSwing, final float limbSwingAmount, final float partialTick, final float ageInTicks, final float netHeadYaw, final float headPitch) {
		if (entitylivingbaseIn instanceof AbstractClientPlayerEntity) {
			AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) entitylivingbaseIn;

			if (player == Minecraft.getInstance().player && !ClientConfigHandler.allowModelForSelf)
				return;
			else if (player != Minecraft.getInstance().player) {
				if (!ClientConfigHandler.allowModelForOthers)
					return;

				boolean renderOrNot = false;

				if (Lizardification.proxy.hasRenderingDecision(player)) {
					renderOrNot = Lizardification.proxy.getRenderingDecision(player);
				} else {
					renderOrNot = false;
				}

				if (!renderOrNot)
					return;
			}

			matrixStackIn.push();
			this.getEntityModel().copyModelAttributesTo(this.model);
			this.model.setPartialTick(entitylivingbaseIn, partialTick);
			this.model.setRotationAngles(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
			final IVertexBuilder ivertexbuilder = ItemRenderer.getBuffer(bufferIn, this.model.getRenderType(((AbstractClientPlayerEntity) entitylivingbaseIn).getLocationSkin()), false, false);
			this.model.render(matrixStackIn, ivertexbuilder, packedLightIn, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
			matrixStackIn.pop();
		}
	}

	public static class PlayerModel<T extends LivingEntity> extends AgeableModel<T> {
		public float swimAnimation;
		private final ModelRenderer snout;
		private final ModelRenderer tail;

		public PlayerModel() {
			this.textureWidth = 64;
			this.textureHeight = 64;
			(this.snout = new ModelRenderer(this)).setRotationPoint(0.0f, 0.0f, 0.0f);
			this.snout.setTextureOffset(24, 0).addBox(-3.0f, -4.0f, -7.0f, 6.0f, 3.0f, 3.0f, 0.0f, false);
			this.snout.setTextureOffset(56, 30).addBox(-1.0f, -5.0f, -6.0f, 2.0f, 1.0f, 2.0f, 0.0f, false);
			(this.tail = new ModelRenderer(this)).setRotationPoint(0.0f, 14.0f, 0.0f);
			this.setRotationAngle(this.tail, -45.0f, 0.0f, 0.0f);
			this.tail.setTextureOffset(56, 33).addBox(-1.0f, 7.0f, -7.0f, 2.0f, 13.0f, 2.0f, 0.0f, false);
		}

		@Override
		public void setRotationAngles(final T entityIn, final float limbSwing, final float limbSwingAmount, final float ageInTicks, final float netHeadYaw, final float headPitch) {
			this.snout.rotateAngleY = netHeadYaw * 0.017453292f;
			this.tail.rotateAngleX = 0.7854f;
			this.tail.rotateAngleY = 0.0f;
			this.snout.rotateAngleX = headPitch * 0.017453292f;
			if (entityIn instanceof PlayerEntity) {
				if (entityIn.isCrouching()) {
					this.snout.rotationPointY = 4.2f;
					this.tail.rotateAngleX = 1.2854f;
					this.tail.rotationPointY = 3.2f;
				} else {
					this.tail.rotationPointY = 0.0f;
					this.tail.rotateAngleX = 0.7854f;
					this.snout.rotationPointY = 0.0f;
				}
			}
			final boolean flag = entityIn.getTicksElytraFlying() > 4;
			final boolean flag2 = entityIn.isActualySwimming();
			this.snout.rotateAngleY = netHeadYaw * 0.017453292f;
			if (flag) {
				this.snout.rotateAngleX = -0.7853982f;
			} else if (this.swimAnimation > 0.0f) {
				if (flag2) {
					this.snout.rotateAngleX = this.rotLerpRad(this.snout.rotateAngleX, -0.7853982f, this.swimAnimation);
				} else {
					this.snout.rotateAngleX = this.rotLerpRad(this.snout.rotateAngleX, headPitch * 0.017453292f, this.swimAnimation);
				}
			} else {
				this.snout.rotateAngleX = headPitch * 0.017453292f;
			}
		}

		@Override
		protected Iterable<ModelRenderer> getHeadParts() {
			return ImmutableList.of(this.snout);
		}

		@Override
		protected Iterable<ModelRenderer> getBodyParts() {
			return ImmutableList.of(this.tail);
		}

		public void setRotationAngle(final ModelRenderer modelRenderer, final float x, final float y, final float z) {
			modelRenderer.rotateAngleX = x;
			modelRenderer.rotateAngleY = y;
			modelRenderer.rotateAngleZ = z;
		}

		protected float rotLerpRad(final float angleIn, final float maxAngleIn, final float mulIn) {
			float f = (maxAngleIn - angleIn) % 6.2831855f;
			if (f < -3.1415927f) {
				f += 6.2831855f;
			}
			if (f >= 3.1415927f) {
				f -= 6.2831855f;
			}
			return angleIn + mulIn * f;
		}

		public void setPartialTick(final T entityIn, final float partialTick) {
			this.swimAnimation = entityIn.getSwimAnimation(partialTick);
		}
	}
}
