package XiGyoku.furyborn.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import XiGyoku.furyborn.Furyborn;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(Font.class)
public abstract class FontMixin {

    @Unique
    private static final String FURYBORN_MARKER = ":_FB_";

    @Unique
    private static final String FURYBORN_STARRY_MARKER = ":_FBS_";

    @Unique
    private static final ResourceLocation ZEN_OLD_MINCHO = new ResourceLocation(Furyborn.MODID, "zen_old_mincho");

    @Unique
    private static final ThreadLocal<Boolean> SKIP_MIXIN = ThreadLocal.withInitial(() -> false);

    @Inject(method = "drawInBatch(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;IIZ)I", at = @At("HEAD"), cancellable = true)
    private void furyborn$drawInBatchString(String text, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int packedLight, int backgroundColor, boolean isTransparent, CallbackInfoReturnable<Integer> cir) {
        if (SKIP_MIXIN.get() || text == null) return;

        boolean hasFB = text.contains(FURYBORN_MARKER);
        boolean hasFBS = text.contains(FURYBORN_STARRY_MARKER);

        if (hasFB || hasFBS) {
            String cleanText = text.replace(FURYBORN_MARKER, "").replace(FURYBORN_STARRY_MARKER, "");
            Font font = (Font) (Object) this;
            int resultWidth = 0;

            SKIP_MIXIN.set(true);
            try {
                if (hasFB) {
                    handleHaloText(cleanText, x, y, matrix, bufferSource, displayMode, packedLight);
                    resultWidth = 1;
                } else if (hasFBS) {
                    resultWidth = font.drawInBatch(cleanText, x, y, color, dropShadow, matrix, bufferSource, displayMode, packedLight, backgroundColor, isTransparent);
                }
            } finally {
                SKIP_MIXIN.set(false);
            }

            cir.setReturnValue(resultWidth);
            cir.cancel();
        }
    }

    @Inject(method = "drawInBatch(Lnet/minecraft/util/FormattedCharSequence;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;II)I", at = @At("HEAD"), cancellable = true)
    private void furyborn$drawInBatchFormatted(FormattedCharSequence sequence, float x, float y, int color, boolean dropShadow, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int packedLight, int backgroundColor, CallbackInfoReturnable<Integer> cir) {
        if (SKIP_MIXIN.get() || sequence == null) return;

        List<Style> styles = new ArrayList<>();
        List<Integer> codepoints = new ArrayList<>();

        sequence.accept((index, style, codepoint) -> {
            styles.add(style);
            codepoints.add(codepoint);
            return true;
        });

        StringBuilder sb = new StringBuilder();
        for (int cp : codepoints) {
            sb.appendCodePoint(cp);
        }
        String fullText = sb.toString();

        boolean hasFB = fullText.contains(FURYBORN_MARKER);
        boolean hasFBS = fullText.contains(FURYBORN_STARRY_MARKER);

        Font font = (Font) (Object) this;
        int resultWidth = 0;

        SKIP_MIXIN.set(true);
        try {
            if (hasFB) {
                String cleanText = fullText.replace(FURYBORN_MARKER, "").replace(FURYBORN_STARRY_MARKER, "");
                handleHaloText(cleanText, x, y, matrix, bufferSource, displayMode, packedLight);
                resultWidth = 1;
                cir.setReturnValue(resultWidth);
                cir.cancel();
            } else if (hasFBS) {
                FormattedCharSequence filtered = createFilteredSequence(styles, codepoints, fullText, FURYBORN_STARRY_MARKER);
                resultWidth = font.drawInBatch(filtered, x, y, color, dropShadow, matrix, bufferSource, displayMode, packedLight, backgroundColor);
                cir.setReturnValue(resultWidth);
                cir.cancel();
            } else {
                FormattedCharSequence reconstructed = createFilteredSequence(styles, codepoints, fullText, null);
                resultWidth = font.drawInBatch(reconstructed, x, y, color, dropShadow, matrix, bufferSource, displayMode, packedLight, backgroundColor);
                cir.setReturnValue(resultWidth);
                cir.cancel();
            }
        } finally {
            SKIP_MIXIN.set(false);
        }
    }

    @Unique
    private FormattedCharSequence createFilteredSequence(List<Style> styles, List<Integer> codepoints, String fullText, String markerToRemove) {
        return sink -> {
            List<Style> finalStyles = new ArrayList<>(styles);
            List<Integer> finalCodepoints = new ArrayList<>(codepoints);

            if (markerToRemove != null) {
                int markerIndex = fullText.indexOf(markerToRemove);
                if (markerIndex != -1) {
                    int markerCodePoints = markerToRemove.codePointCount(0, markerToRemove.length());
                    int codePointIndex = fullText.codePointCount(0, markerIndex);

                    for (int i = 0; i < markerCodePoints; i++) {
                        if (codePointIndex < finalStyles.size()) {
                            finalStyles.remove(codePointIndex);
                            finalCodepoints.remove(codePointIndex);
                        }
                    }
                }
            }

            int newIndex = 0;
            for (int i = 0; i < finalStyles.size(); i++) {
                if (!sink.accept(newIndex++, finalStyles.get(i), finalCodepoints.get(i))) {
                    return false;
                }
            }
            return true;
        };
    }

    @Unique
    private void handleHaloText(String str, float x, float y, Matrix4f matrix, MultiBufferSource bufferSource, Font.DisplayMode displayMode, int packedLight) {
        Font font = (Font) (Object) this;
        long time = System.currentTimeMillis() / 50;

        float currentX = x - 1.0F;
        float currentY = y + 2.0F;

        int fullLight = 15728880;

        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        Style baseStyle = Style.EMPTY.withFont(ZEN_OLD_MINCHO);

        for (int i = 0; i < str.length(); i++) {
            String charStr = String.valueOf(str.charAt(i));
            double wave = (Math.sin((time / 6.0) - (i * 0.4)) + 1.0) / 2.0;
            int r = (int) (0x66 + (0x99 * wave));
            int g = 0xFF;
            int b = (int) (0x66 + (0x99 * wave));
            int animatedColor = (0xFF << 24) | (r << 16) | (g << 8) | b;

            Style charStyle = baseStyle.withColor(TextColor.fromRgb(animatedColor & 0xFFFFFF));
            FormattedCharSequence charSeq = Component.literal(charStr).withStyle(charStyle).getVisualOrderText();

            font.drawInBatch(charSeq, currentX, currentY, animatedColor, false, matrix, bufferSource, displayMode, 0, fullLight);

            currentX += font.width(Component.literal(charStr).withStyle(baseStyle));
        }
    }
}