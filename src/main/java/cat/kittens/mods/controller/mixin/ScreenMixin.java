package cat.kittens.mods.controller.mixin;

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import cat.kittens.mods.controller.ui.ScrollableScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = Screen.class, remap = false)
public class ScreenMixin {
    @Inject(
            method = "tickInput",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/gui/screen/Screen;onMouseEvent()V",
                    shift = At.Shift.AFTER
            )
    )
    void ui$scrollableScreenInputHandling(CallbackInfo callback) {
        Minecraft mc = MinecraftAccessor.instance();
        var screen = (Screen) (Object) this;
        if (screen instanceof ScrollableScreen s) {
            int x = Mouse.getEventX() * screen.width / mc.displayWidth,
                y = screen.height - Mouse.getEventY() * screen.height / mc.displayHeight - 1;
            s.handleScroll(x, y);
        }
    }
}
