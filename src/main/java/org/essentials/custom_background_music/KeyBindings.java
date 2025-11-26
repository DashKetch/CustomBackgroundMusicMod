package org.essentials.custom_background_music;

import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.settings.KeyConflictContext;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "custom_background_music", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {

    public static final Lazy<KeyMapping> OPEN_MUSIC_GUI = Lazy.of(() -> {
        KeyMapping mapping = new KeyMapping(
                "key.custom_background_music.open_music_gui", // translation key
                GLFW.GLFW_KEY_P,                              // default key
                "key.categories.custom_background_music"                         // category string
        );

        //only works when no GUI is open
        mapping.setKeyConflictContext(KeyConflictContext.IN_GAME);

        return mapping;
    });

    @SubscribeEvent
    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MUSIC_GUI.get());
    }
}
