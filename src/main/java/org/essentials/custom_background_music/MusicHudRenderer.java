package org.essentials.custom_background_music;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.neoforged.bus.api.SubscribeEvent; // Try .api instead of .event
import net.neoforged.neoforge.client.event.RenderGuiEvent;

public class MusicHudRenderer {

    @SubscribeEvent
    public void onRenderGui(RenderGuiEvent.Post event) {
        Minecraft mc = Minecraft.getInstance();
        AudioManager audio = AudioManager.getInstance();

        if (audio.hasLoadedMusic() && (audio.isPlaying() || audio.isPaused())) {
            GuiGraphics graphics = event.getGuiGraphics();

            String songNameFull = audio.getCurrentFileName();
            String status = audio.isPaused() ? "Paused: " : "Now Playing: ";
            String songName = songNameFull.replace(".mp3", "");
            String fullText = status + songName;

            int x = 10;
            int y = 10;
            int width = mc.font.width(fullText);

            // Draw background
            graphics.fill(x - 4, y - 4, x + width + 4, y + 12, 0x99000000);

            // Draw text
            graphics.drawString(mc.font, fullText, x, y, 0xFFFFFF, true);
        }
    }
}