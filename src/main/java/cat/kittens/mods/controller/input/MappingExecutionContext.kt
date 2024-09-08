package cat.kittens.mods.controller.input

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import net.minecraft.client.Minecraft

/**
 * Provides information about an active controller mapping.
 *
 * @param value Either a floating point number for an analog input, or just 1.0 for digital inputs.
 * @param held  Whether this context is being active for more than one cycle.
 */
public data class MappingExecutionContext(val value: Double, val held: Boolean) {
    inline val minecraft: Minecraft get() = MinecraftAccessor.instance()
}

public inline val Minecraft.asAccessor: MinecraftAccessor get() = this as MinecraftAccessor

public inline val MinecraftAccessor.asMinecraft: Minecraft get() = this as Minecraft