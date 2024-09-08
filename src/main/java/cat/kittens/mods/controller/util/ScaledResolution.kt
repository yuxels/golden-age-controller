package cat.kittens.mods.controller.util

import net.minecraft.client.option.GameOptions
import kotlin.math.ceil

private fun GameOptions.guiScale(width: Int, height: Int): Double {
    var guiScale2 = 1.0
    val scaling = if (guiScale == 0) 1000 else guiScale
    while (guiScale2 < scaling && width / (guiScale2 + 1) >= 320 && height / (guiScale2 + 1) >= 240) {
        ++guiScale2
    }
    return guiScale2
}

public data class ScaledResolution(
    val width: Int,
    val height: Int,
    val guiScaledWidth: Double,
    val guiScaledHeight: Double,
    val guiScale: Int
) {
    public constructor(gameOptions: GameOptions, width: Int, height: Int) : this(
        ceil(width.toDouble() / gameOptions.guiScale(width, height)).toInt(),
        ceil(height.toDouble() / gameOptions.guiScale(width, height)).toInt(),
        width.toDouble() / gameOptions.guiScale(width, height),
        height.toDouble() / gameOptions.guiScale(width, height),
        gameOptions.guiScale(width, height).toInt()
    )
}
