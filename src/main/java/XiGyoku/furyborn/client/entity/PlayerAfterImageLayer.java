package XiGyoku.furyborn.client.entity;

import XiGyoku.furyborn.client.util.ClientAfterImageManager;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class PlayerAfterImageLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerAfterImageLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (!player.getPersistentData().getBoolean("ExolumenAfterImage")) {
            return;
        }

        List<Vec3> pastPositions = ClientAfterImageManager.PAST_POSITIONS.get(player.getUUID());
        if (pastPositions == null || pastPositions.isEmpty()) return;

        RenderType renderType = RenderType.entityTranslucent(player.getSkinTextureLocation());
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        float r = 1.0F;
        float g = 0.6F;
        float b = 0.6F;
        int fullBright = 15728880;

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.9D, 0.0D);
        poseStack.scale(1.05F, 1.05F, 1.05F);
        poseStack.translate(0.0D, -0.9D, 0.0D);
        this.getParentModel().renderToBuffer(poseStack, buffer, fullBright, OverlayTexture.NO_OVERLAY, r, g, b, 0.25F);
        poseStack.popPose();

        this.getParentModel().renderToBuffer(poseStack, buffer, fullBright, OverlayTexture.NO_OVERLAY, r, g, b, 0.45F);

        if (pastPositions.size() < 2) return;

        double lerpX = Mth.lerp(partialTick, player.xo, player.getX());
        double lerpY = Mth.lerp(partialTick, player.yo, player.getY());
        double lerpZ = Mth.lerp(partialTick, player.zo, player.getZ());

        float bodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);

        int maxImages = Math.min(pastPositions.size(), 20);

        for (int i = 3; i < maxImages; i += 3) {
            Vec3 past = pastPositions.get(i);

            double dx = past.x - lerpX;
            double dy = past.y - lerpY;
            double dz = past.z - lerpZ;

            if (dx * dx + dy * dy + dz * dz < 0.005D) {
                continue;
            }

            float alpha = 0.6F - (i * 0.03F);

            if (alpha > 0.0F) {
                poseStack.pushPose();
                poseStack.translate(0.0D, 1.501D, 0.0D);
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(bodyYaw - 180.0F));
                poseStack.translate(dx, dy, dz);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - bodyYaw));
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                poseStack.translate(0.0D, -1.501D, 0.0D);

                this.getParentModel().renderToBuffer(poseStack, buffer, fullBright, OverlayTexture.NO_OVERLAY, r, g, b, alpha);
                poseStack.popPose();
            }
        }
    }
}