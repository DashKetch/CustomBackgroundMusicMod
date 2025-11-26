package org.essentials.custom_background_music;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = CustomBackgroundMusic.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {

    public static final KeyMapping OPEN_MUSIC_GUI = new KeyMapping(
            "key.custom_background_music.open_music_gui",
            GLFW.GLFW_KEY_M,
            "key.categories.custom_background_music"
    );

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MUSIC_GUI);
    }
}