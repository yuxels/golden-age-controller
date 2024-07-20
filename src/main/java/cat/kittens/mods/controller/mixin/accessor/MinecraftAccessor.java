package cat.kittens.mods.controller.mixin.accessor;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.Timer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = Minecraft.class, remap = false)
public interface MinecraftAccessor {
    @Invoker("method_2107")
    void interact(int type);

    @Invoker("method_2103")
    void pickBlock();

    @Accessor("field_2798")
    int lastInteraction();

    @Accessor("field_2798")
    void setLastInteraction(int ticks);

    @Invoker("method_2110")
    void holdInteract(int type, boolean active);

    @Accessor("ticksPlayed")
    int currentTicks();

    @Accessor("timer")
    Timer timer();

    @Accessor("field_2778")
    boolean mouseGrabbed();

    @Accessor("field_2778")
    void setMouseGrabbed(boolean mouseGrabbed);

    @Accessor("INSTANCE")
    static Minecraft instance() {
        throw new AssertionError();
    }
}
