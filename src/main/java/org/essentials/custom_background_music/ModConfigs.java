package org.essentials.custom_background_music;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfigs {
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();

    public static final ModConfigSpec.BooleanValue SHOW_HUD;
    public static final ModConfigSpec.BooleanValue RESTRICT_VOLUME;
    public static final ModConfigSpec.BooleanValue DYNAM_BUTTONS;
    public static final ModConfigSpec.IntValue HUD_X;
    public static final ModConfigSpec.IntValue HUD_Y;
    public static final ModConfigSpec.ConfigValue<String> HUD_COLOR;

    static {
        BUILDER.push("Music HUD Settings");

        SHOW_HUD = BUILDER.define("show_hud", true);
        HUD_X = BUILDER.defineInRange("hud_x", 10, 0, 4000);
        HUD_Y = BUILDER.defineInRange("hud_y", 10, 0, 4000);
        HUD_COLOR = BUILDER.define("hud_text_color", "FFFFFF");
        RESTRICT_VOLUME = BUILDER.define("push_volume_past_100", false);
        DYNAM_BUTTONS = BUILDER.define("dyanmic_buttons", true);

        BUILDER.pop();
    }

    public static final ModConfigSpec SPEC = BUILDER.build();
}