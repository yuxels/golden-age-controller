package cat.kittens.mods.controller.util;

import net.minecraft.client.option.GameOptions;

public record ScaledResolution(
     int width,
     int height,
     double guiScaledWidth,
     double guiScaledHeight,
     int guiScale
) {
    public static ScaledResolution create(GameOptions gameOptions, int width, int height) {
        var guiScale = 1;
        int scaling = gameOptions.guiScale == 0 ? 1000 : gameOptions.guiScale;
        while(guiScale < scaling && width / (guiScale + 1) >= 320 && height / (guiScale + 1) >= 240) {
            ++guiScale;
        }
        var guiScaledWidth = (double)width / (double)guiScale;
        var guiScaledHeight = (double)height / (double)guiScale;
        return new ScaledResolution(
                (int)Math.ceil(guiScaledWidth),
                (int)Math.ceil(guiScaledHeight),
                guiScaledWidth,
                guiScaledHeight,
                guiScale
        );
    }

}
