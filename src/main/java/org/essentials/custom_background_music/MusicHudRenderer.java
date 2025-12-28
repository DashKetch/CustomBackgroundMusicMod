package org.essentials.custom_background_music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

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

            int x = ModConfigs.HUD_X.get();
            int y = ModConfigs.HUD_Y.get();

            int color;
            try {
                String hex = ModConfigs.HUD_COLOR.get().replace("#", "");
                color = Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
                color = 0xFFFFFF; // Default to white
            }

            int width = mc.font.width(fullText);

            // Draw background box (Semi-transparent black)
            graphics.fill(x - 4, y - 4, x + width + 4, y + 12, 0x99000000);

            // Draw the text with a shadow
            graphics.drawString(mc.font, fullText, x, y, color, true);
        }
    }
}