package org.essentials.custom_background_music;

import net.neoforged.neoforge.common.ModConfigSpec;

public class ModConfigs {
    public static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final ModConfigSpec SPEC;

    // HUD Settings
    public static final ModConfigSpec.BooleanValue SHOW_HUD;
    public static final ModConfigSpec.IntValue HUD_X;
    public static final ModConfigSpec.IntValue HUD_Y;
    public static final ModConfigSpec.ConfigValue<String> HUD_COLOR;

    static {
        BUILDER.push("Music HUD Settings");

        SHOW_HUD = BUILDER
                .comment("Whether to show the 'Now Playing' HUD.")
                .define("show_hud", true);

        HUD_X = BUILDER
                .comment("The X position of the HUD.")
                .defineInRange("hud_x", 10, 0, 4000);

        HUD_Y = BUILDER
                .comment("The Y position of the HUD.")
                .defineInRange("hud_y", 10, 0, 4000);

        HUD_COLOR = BUILDER
                .comment("The hex color of the HUD text (e.g., #FFFFFF).")
                .define("hud_color", "FFFFFF");

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}