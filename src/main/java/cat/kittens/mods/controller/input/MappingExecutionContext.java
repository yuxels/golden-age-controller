package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.Minecraft;

/**
 * Provides information about an active controller mapping.
 *
 * @param value Either a floating point number for an analog input, or just 1.0 for digital inputs.
 * @param held  Whether this context is being active for more than one cycle.
 */
public record MappingExecutionContext(double value, boolean held) {
    public Minecraft minecraft() {
        return MinecraftAccessor.instance();
    }

    public MinecraftAccessor mc() {
        return (MinecraftAccessor) minecraft();
    }
}