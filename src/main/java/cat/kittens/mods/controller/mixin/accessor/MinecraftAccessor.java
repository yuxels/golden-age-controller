package cat.kittens.mods.controller.mixin.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Minecraft.class)
public interface MinecraftAccessor {
    @Invoker("method_2107")
    void interact(int type);

    @Invoker("method_2103")
    void pickBlock();

    @Accessor("field_2798")
    int lastInteraction();

    @Accessor("field_2798")
    void setLastInteraction(int ticks);

    @Accessor("ticksPlayed")
    int currentTicks();

    @Accessor("timer")
    Timer timer();

    @Accessor("INSTANCE")
    static Minecraft instance() {
        throw new AssertionError();
    }
}
