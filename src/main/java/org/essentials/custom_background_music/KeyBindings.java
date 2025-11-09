// ========== KeyBindings.java ==========
package org.essentials.custom_background_music;

import net.minecraft.client.KeyMapping;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final KeyMapping OPEN_MUSIC_GUI = new KeyMapping(
            "key.custom_background_music.open_gui",
            GLFW.GLFW_KEY_M,
            KeyMapping.Category.MISC
    );

    public static void register(RegisterKeyMappingsEvent event) {
        event.register(OPEN_MUSIC_GUI);
    }
}
