package org.essentials.custom_background_music;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.common.util.Lazy;
import org.lwjgl.glfw.GLFW;

@EventBusSubscriber(modid = "custom_background_music", bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class KeyBindings {

    public static final String CATEGORY = "key.categories.custom_background_music";

    public static final Lazy<KeyMapping> OPEN_MUSIC_GUI = Lazy.of(() ->
            new KeyMapping(
                    "key.custom_background_music.open_music_gui",
                    InputConstants.Type.KEYSYM,
                    GLFW.GLFW_KEY_M,
                    CATEGORY
            )
    );

    public static void registerBindings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MUSIC_GUI.get());
    }
}
