package XiGyoku.furyborn.client.gui;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class DriveshiftTintOverlay {
    private static final ResourceLocation VIGNETTE_LOCATION = new ResourceLocation("minecraft", "textures/misc/vignette.png");

    public static void render(GuiGraphics guiGraphics, float partialTick, int width, int height) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        float fadeProgress = player.getPersistentData().contains("DriveshiftFadeProgress") ? player.getPersistentData().getFloat("DriveshiftFadeProgress") : 0.0f;

        if (fadeProgress <= 0.0F) return;

        int greenTicks = player.getPersistentData().contains("DriveshiftGreenTicks") ? player.getPersistentData().getInt("DriveshiftGreenTicks") : 0;

        float r = 1.0F;
        float g = 0.0F;
        float b = 0.0F;

        if (greenTicks > 20) {
            r = 0.0F;
            g = 1.0F;
            b = 0.0F;
        } else if (greenTicks > 0) {
            float ratio = greenTicks / 20.0F;
            r = Mth.lerp(ratio, 1.0F, 0.0F);
            g = Mth.lerp(ratio, 0.0F, 1.0F);
            b = 0.0F;
        }

        float alpha = fadeProgress * 0.85F;
        if (alpha <= 0.01F) return;

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();

        RenderSystem.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE);

        RenderSystem.setShaderColor(r, g, b, alpha);

        guiGraphics.blit(VIGNETTE_LOCATION, 0, 0, -90, 0.0F, 0.0F, width, height, width, height);

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
        RenderSystem.defaultBlendFunc();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
    }
}