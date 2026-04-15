package XiGyoku.furyborn.client.entity;

import XiGyoku.furyborn.client.util.AfterImageData;
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

import java.util.List;

public class PlayerAfterImageLayer extends RenderLayer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> {

    public PlayerAfterImageLayer(RenderLayerParent<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, AbstractClientPlayer player, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        boolean isActive = player.getPersistentData().getBoolean("ExolumenAfterImage");
        float fadeProgress = player.getPersistentData().contains("DriveshiftFadeProgress") ? player.getPersistentData().getFloat("DriveshiftFadeProgress") : 0.0f;

        if (!isActive && fadeProgress <= 0.0F) {
            return;
        }

        List<AfterImageData> pastPositions = ClientAfterImageManager.PAST_POSITIONS.get(player.getUUID());
        if (pastPositions == null || pastPositions.isEmpty()) return;

        RenderType renderType = RenderType.entityTranslucent(player.getSkinTextureLocation());
        VertexConsumer buffer = bufferSource.getBuffer(renderType);

        float r = 1.0F;
        float g = 0.6F;
        float b = 0.6F;

        int greenTicks = player.getPersistentData().contains("DriveshiftGreenTicks") ? player.getPersistentData().getInt("DriveshiftGreenTicks") : 0;

        if (greenTicks > 20) {
            r = 0.0F;
            g = 1.0F;
            b = 0.0F;
        } else if (greenTicks > 0) {
            float ratio = greenTicks / 20.0F;
            r = Mth.lerp(ratio, 1.0F, 0.0F);
            g = Mth.lerp(ratio, 0.6F, 1.0F);
            b = Mth.lerp(ratio, 0.6F, 0.0F);
        }

        int fullBright = 15728880;

        poseStack.pushPose();
        poseStack.translate(0.0D, 0.9D, 0.0D);
        poseStack.scale(1.05F, 1.05F, 1.05F);
        poseStack.translate(0.0D, -0.9D, 0.0D);
        this.getParentModel().renderToBuffer(poseStack, buffer, fullBright, OverlayTexture.NO_OVERLAY, r, g, b, 0.25F * fadeProgress);
        poseStack.popPose();

        this.getParentModel().renderToBuffer(poseStack, buffer, fullBright, OverlayTexture.NO_OVERLAY, r, g, b, 0.45F * fadeProgress);

        if (pastPositions.size() < 2) return;

        double lerpX = Mth.lerp(partialTick, player.xo, player.getX());
        double lerpY = Mth.lerp(partialTick, player.yo, player.getY());
        double lerpZ = Mth.lerp(partialTick, player.zo, player.getZ());

        float currentBodyYaw = Mth.rotLerp(partialTick, player.yBodyRotO, player.yBodyRot);

        int maxImages = Math.min(pastPositions.size(), 20);

        for (int i = 3; i < maxImages; i += 3) {
            AfterImageData past = pastPositions.get(i);

            double dx = past.position.x - lerpX;
            double dy = past.position.y - lerpY;
            double dz = past.position.z - lerpZ;

            if (dx * dx + dy * dy + dz * dz < 0.005D) {
                continue;
            }

            float alpha = (0.6F - (i * 0.03F)) * fadeProgress;

            if (alpha > 0.0F) {
                this.getParentModel().setupAnim(player, past.limbSwing, past.limbSwingAmount, past.ageInTicks, past.netHeadYaw, past.headPitch);

                poseStack.pushPose();
                poseStack.translate(0.0D, 1.501D, 0.0D);
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(currentBodyYaw - 180.0F));
                poseStack.translate(dx, dy, dz);
                poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(180.0F - past.bodyYaw));
                poseStack.scale(-1.0F, -1.0F, 1.0F);
                poseStack.translate(0.0D, -1.501D, 0.0D);

                this.getParentModel().renderToBuffer(poseStack, buffer, fullBright, OverlayTexture.NO_OVERLAY, r, g, b, alpha);
                poseStack.popPose();
            }
        }

        this.getParentModel().setupAnim(player, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}