package org.essentials.custom_background_music;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ClientTickEvent;

@EventBusSubscriber(modid = CustomBackgroundMusic.MODID, value = Dist.CLIENT)
public class InputHandler {

    @SubscribeEvent
    public static void onClientTick(ClientTickEvent.Post event) {
        // Check if the key we registered was pressed
        while (KeyBindings.OPEN_MUSIC_GUI.consumeClick()) {
            MusicGuiScreen.open();
        }
    }
}