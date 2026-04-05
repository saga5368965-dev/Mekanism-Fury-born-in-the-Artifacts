package XiGyoku.furyborn.client.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import mekanism.client.render.item.MekanismISTER;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RenderBusterThrower extends MekanismISTER {

    public static final RenderBusterThrower RENDERER = new RenderBusterThrower();
    private ModelBusterThrower busterThrower;

    @Override
    public void onResourceManagerReload(@NotNull ResourceManager resourceManager) {
        if (Minecraft.getInstance() != null) {
            busterThrower = new ModelBusterThrower(Minecraft.getInstance().getEntityModels());
        }
    }

    @Override
    public void renderByItem(@NotNull ItemStack stack, @NotNull ItemDisplayContext displayContext, @NotNull PoseStack matrix, @NotNull MultiBufferSource renderer,
                             int light, int overlayLight) {
        if (busterThrower == null) {
            busterThrower = new ModelBusterThrower(Minecraft.getInstance().getEntityModels());
        }

        matrix.pushPose();
        matrix.translate(0.5, 0.5, 0.5);
        matrix.mulPose(Axis.ZP.rotationDegrees(180));
        busterThrower.render(matrix, renderer, light, overlayLight, stack.hasFoil());
        matrix.popPose();
    }
}