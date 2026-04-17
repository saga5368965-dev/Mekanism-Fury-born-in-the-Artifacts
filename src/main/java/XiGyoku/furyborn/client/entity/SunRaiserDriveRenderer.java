package XiGyoku.furyborn.client.entity;

import XiGyoku.furyborn.client.entity.SunRaiserDriveModel;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;
import top.theillusivec4.curios.api.SlotContext;
import top.theillusivec4.curios.api.SlotResult;
import top.theillusivec4.curios.api.client.ICurioRenderer;

import java.util.List;

public class SunRaiserDriveRenderer implements ICurioRenderer {

    private static final ResourceLocation TEXTURE = new ResourceLocation("furyborn", "textures/item/sunraiser_drive.png");
    private final SunRaiserDriveModel<LivingEntity> model;

    public SunRaiserDriveRenderer() {
        this.model = new SunRaiserDriveModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(SunRaiserDriveModel.LAYER_LOCATION));
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T extends LivingEntity, M extends EntityModel<T>> void render(ItemStack stack, SlotContext slotContext, PoseStack matrixStack, RenderLayerParent<T, M> renderLayerParent, MultiBufferSource renderTypeBuffer, int light, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        matrixStack.pushPose();

        if (renderLayerParent.getModel() instanceof HumanoidModel) {
            ICurioRenderer.followBodyRotations(slotContext.entity(), (HumanoidModel<LivingEntity>) renderLayerParent.getModel());
        }

        List<SlotResult> equippedResults = CuriosApi.getCuriosHelper().findCurios(slotContext.entity(), stack.getItem());
        int totalEquipped = equippedResults.size();

        if (totalEquipped >= 2) {
            int renderIndex = 0;
            for (int i = 0; i < equippedResults.size(); i++) {
                SlotContext ctx = equippedResults.get(i).slotContext();
                if (ctx.identifier().equals(slotContext.identifier()) && ctx.index() == slotContext.index()) {
                    renderIndex = i;
                    break;
                }
            }

            if (renderIndex % 2 == 0) {
                matrixStack.translate(-0.550, -0.125, -0.075);
                matrixStack.mulPose(Axis.YP.rotationDegrees(0.0F));
            } else {
                matrixStack.scale(-1.0F, 1.0F, 1.0F);
                matrixStack.translate(-0.550, -0.125, -0.075);
                matrixStack.mulPose(Axis.YP.rotationDegrees(0.0F));
            }
        } else {
            matrixStack.translate(0.0, -0.125, 0.25);
            matrixStack.mulPose(Axis.YP.rotationDegrees(90.0F));
        }

        matrixStack.scale(0.25F, 0.25F, 0.25F);

        VertexConsumer vertexConsumer = ItemRenderer.getArmorFoilBuffer(renderTypeBuffer, RenderType.entityTranslucent(TEXTURE), false, stack.hasFoil());
        this.model.renderToBuffer(matrixStack, vertexConsumer, light, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        matrixStack.popPose();
    }
}