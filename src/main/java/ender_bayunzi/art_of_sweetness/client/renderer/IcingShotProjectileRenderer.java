package ender_bayunzi.art_of_sweetness.client.renderer;

import org.joml.Matrix4f;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;

import ender_bayunzi.art_of_sweetness.ArtOfSweetness;
import ender_bayunzi.art_of_sweetness.client.init.ModRenderTypes;
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
		poseStack.pushPose();
		poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTicks, entityIn.yRotO, entityIn.getYRot()) - 90));
		poseStack.mulPose(Axis.ZP.rotationDegrees(90 + Mth.lerp(partialTicks, entityIn.xRotO, entityIn.getXRot())));
		
		poseStack.pushPose();
		
		poseStack.translate(0, -0.4, 0);
		
		VertexConsumer consumer = bufferIn.getBuffer(ModRenderTypes.NO_CULL);
		PoseStack.Pose pose = poseStack.last();
        Matrix4f poseMat = pose.pose();

        float r = 1, g = 1, b = 1;
        float radius = 0.1f;
		int slices = 16;
        int stacks = 8;

        for (int i = 0; i < slices; i++) {
            float theta1 = ((float) i / slices) * Mth.TWO_PI;
            float theta2 = ((float) (i + 1) / slices) * Mth.TWO_PI;

            for (int j = 0; j < stacks; j++) {
                float phi1 = ((float) j / stacks) * Mth.PI;
                float phi2 = ((float) (j + 1) / stacks) * Mth.PI;

                float x1 = Mth.sin(phi1) * Mth.cos(theta1) * radius;
                float y1 = Mth.cos(phi1) * radius;
                float z1 = Mth.sin(phi1) * Mth.sin(theta1) * radius;

                float x2 = Mth.sin(phi1) * Mth.cos(theta2) * radius;
                float y2 = Mth.cos(phi1) * radius;
                float z2 = Mth.sin(phi1) * Mth.sin(theta2) * radius;

                float x3 = Mth.sin(phi2) * Mth.cos(theta2) * radius;
                float y3 = Mth.cos(phi2) * radius;
                float z3 = Mth.sin(phi2) * Mth.sin(theta2) * radius;

                float x4 = Mth.sin(phi2) * Mth.cos(theta1) * radius;
                float y4 = Mth.cos(phi2) * radius;
                float z4 = Mth.sin(phi2) * Mth.sin(theta1) * radius;

                addVertex(consumer, pose, poseMat, x1, y1, z1, r, g, b, packedLightIn);
                addVertex(consumer, pose, poseMat, x2, y2, z2, r, g, b, packedLightIn);
                addVertex(consumer, pose, poseMat, x3, y3, z3, r, g, b, packedLightIn);
                addVertex(consumer, pose, poseMat, x4, y4, z4, r, g, b, packedLightIn);
            }
        }
		
		poseStack.popPose();
		
		VertexConsumer vb = bufferIn.getBuffer(RenderType.entityCutout(this.getTextureLocation(entityIn)));
		model.setupAnim(entityIn, 0, 0, entityIn.tickCount + partialTicks, entityIn.getYRot(), entityIn.getXRot());
		model.renderToBuffer(poseStack, vb, packedLightIn, OverlayTexture.NO_OVERLAY);
		poseStack.popPose();
		super.render(entityIn, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn);
	}

	@Override
	public ResourceLocation getTextureLocation(IcingShotProjectile entity) {
		return texture;
	}
	
	private void addVertex(VertexConsumer consumer, PoseStack.Pose pose, Matrix4f poseMat,
            float x, float y, float z,
            float r, float g, float b,
            int packedLight) {
		consumer.addVertex(poseMat, x, y, z)
		.setColor(r, g, b, 1.0F)
		.setUv(0.0F, 0.0F).setUv2(packedLight & 0xFFFF, packedLight >> 16 & 0xFFFF)
		.setOverlay(OverlayTexture.NO_OVERLAY)
		.setNormal(pose, 0, 1, 0);
	}
    
}
