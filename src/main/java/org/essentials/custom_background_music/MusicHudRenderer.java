package org.essentials.custom_background_music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class MusicHudRenderer {

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        // 1. ADD THIS SAFETY CHECK:
        // This prevents the "Cannot get config value before config is loaded" error
        if (!ModConfigs.SPEC.isLoaded()) {
            return;
        }

        // 2. Check if HUD is enabled in config
        if (!ModConfigs.SHOW_HUD.get()) return;

        Minecraft mc = Minecraft.getInstance();
        AudioManager audio = AudioManager.getInstance();

        // Hide if F3 is open
        //if (mc.options.renderDebug) return;

        if (audio.hasLoadedMusic() && (audio.isPlaying() || audio.isPaused())) {
            GuiGraphics graphics = event.getGuiGraphics();

            String fullText = (audio.isPaused() ? "Paused: " : "Now Playing: ") + audio.getCurrentFileName();

            int x = ModConfigs.HUD_X.get();
            int y = ModConfigs.HUD_Y.get();

            int color;
            try {
                color = Integer.parseInt(ModConfigs.HUD_COLOR.get(), 16);
            } catch (NumberFormatException e) {
                color = 0xFFFFFF;
            }

            int width = mc.font.width(fullText);
            graphics.fill(x - 4, y - 4, x + width + 4, y + 12, 0x99000000);
            graphics.drawString(mc.font, fullText, x, y, color, true);
        }
    }
}