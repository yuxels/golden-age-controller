package cat.kittens.mods.controller.mixin;

import cat.kittens.mods.controller.ControllerSupport;
import net.minecraft.class_413;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = class_413.class, remap = false)
public class KeyboardInputMixin {
    @Inject(method = "method_1942", at = @At(value = "TAIL"))
    void controller$tick(CallbackInfo ignored) {
        ControllerSupport.support().movement().tick();
    }
}
