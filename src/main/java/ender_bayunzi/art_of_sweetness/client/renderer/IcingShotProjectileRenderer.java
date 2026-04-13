package ender_bayunzi.art_of_sweetness.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.client.model.ModelIcingShotProjectile;
import ender_bayunzi.art_of_sweetness.entity.IcingShotProjectile;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

public class IcingShotProjectileRenderer extends EntityRenderer<IcingShotProjectile> {
	private static final ResourceLocation texture = ResourceLocation.fromNamespaceAndPath(ArtOfSweetness.MODID, "textures/entities/icing_shot_projectile.png");
	private final ModelIcingShotProjectile<IcingShotProjectile> model;

	public IcingShotProjectileRenderer(EntityRendererProvider.Context context) {
		super(context);
		model = new ModelIcingShotProjectile<IcingShotProjectile>(context.bakeLayer(ModelIcingShotProjectile.LAYER_LOCATION));
	}

	@Override
	public void render(IcingShotProjectile entityIn, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int packedLightIn) {
		VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		model.setupAnim(entityIn, 0, 0, entityIn.tickCount + partialTicks, entityIn.getYRot(), entityIn.getXRot());
		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(IcingShotProjectile entity) {
		return texture;
	}
}
