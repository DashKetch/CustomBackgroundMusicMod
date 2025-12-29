package org.essentials.custom_background_music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

import static org.essentials.custom_background_music.CustomBackgroundMusic.LOGGER;

public class MusicHudRenderer {

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        // 1. Safety check to prevent the "Config not loaded" crash
        if (!ModConfigs.SPEC.isLoaded()) return;

        // 2. Check if HUD is enabled and STOP if it's false
        boolean show = ModConfigs.SHOW_HUD.get();
        if (!show) return;

        Minecraft mc = Minecraft.getInstance();
        AudioManager audio = AudioManager.getInstance();

        // 3. Only draw if music is actually active
        if (audio.hasLoadedMusic() && (audio.isPlaying() || audio.isPaused())) {
            GuiGraphics graphics = event.getGuiGraphics();

            String fullText = (audio.isPaused() ? "Paused: " : "Now Playing: ") + audio.getCurrentFileName().replace(".mp3", "");
            final ResourceLocation spritePlay = ResourceLocation.fromNamespaceAndPath("custom_background_music", "icons/play");
            final ResourceLocation spritePause = ResourceLocation.fromNamespaceAndPath("custom_background_music", "icons/pause");
            final ResourceLocation spriteForward = ResourceLocation.fromNamespaceAndPath("custom_background_music", "icons/forward");
            final ResourceLocation spriteReverse = ResourceLocation.fromNamespaceAndPath("custom_background_music", "icons/reverse");
            ResourceLocation currentIcon = audio.isPaused() ? spritePause : spritePlay;

            int x = ModConfigs.HUD_X.get();
            int y = ModConfigs.HUD_Y.get();
            int iconSize = 12; // Size in pixels
            int iconY = y - 2; // Aligning with text height
            int width = mc.font.width(fullText);

            // Draw Icons
            try {
                //LOGGER.warn("Trying to render icons");
                graphics.blitSprite(currentIcon, x + width + 15, iconY + 1, iconSize - 3, iconSize - 3);
                graphics.blitSprite(spriteReverse, x + width + 3, iconY, iconSize, iconSize);
                graphics.blitSprite(spriteForward, x + width + 24, iconY, iconSize, iconSize);
            } catch (Exception e) {
                LOGGER.warn("Error while rendering icons", e);
            }

            int color;
            try {
                String hex = ModConfigs.HUD_COLOR.get().replace("#", "");
                color = Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
                color = 0xFFFFFF; // Default to white
            }

            // Draw background box (Semi-transparent black)
            graphics.fill(x - 4, y - 4, x + width + 4, y + 12, 0x99000000);

            // Draw the text with a shadow
            graphics.drawString(mc.font, fullText, x, y, color, true);
        }
    }
}