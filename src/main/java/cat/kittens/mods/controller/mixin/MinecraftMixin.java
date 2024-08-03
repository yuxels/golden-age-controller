package cat.kittens.mods.controller.mixin;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.input.MappingActions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    // Prevent LWJGL2's controller API to be initialized, since we are going to use SDL3 for this.
    @Redirect(
            method = "init",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Controllers;create()V")
    )
    void controller$preventLWJGL2ControllerInit() {
    }
    // end

    // Tick controller inputs.
    @Inject(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/class_555;method_1844(F)V"
            )
    )
    void controller$tick(CallbackInfo callback) {
        ControllerSupport.support().inputProcessing().tickMappings();
    }
    // end

    // Bring back keyboard input method.
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/ClientPlayerEntity;method_136(IZ)V",
                    shift = At.Shift.AFTER
            )
    )
    void controller$keyboard$setCurrentInputMethod(CallbackInfo ci) {
        if (Keyboard.getEventKeyState())
            ControllerSupport.support().setCurrentInputMethod(false);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;method_2103()V",
                    shift = At.Shift.BEFORE
            )
    )
    void controller$mouse$setCurrentInputMethod$2(CallbackInfo ci) {
        if (Mouse.getEventButtonState())
            ControllerSupport.support().setCurrentInputMethod(false);
    }
    // end

    // Inject internal controller mapping logic.
    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z",
                    ordinal = 0
            )
    )
    boolean controller$injectControllerBreak(boolean original) {
        if (original || !ControllerSupport.support().isCurrentInputMethodController()) {
            ControllerSupport.support().setCurrentInputMethod(false);
            return original;
        }
        var controller = ControllerSupport.support().manager().currentController().orElse(null);
        if (controller == null)
            return false;
        return ControllerSupport.support().mapping().find(MappingActions.BREAK)
                .flatMap(m -> m.getContextFor(controller))
                .isPresent();
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z",
                    ordinal = 2
            )
    )
    boolean controller$injectControllerHoldBreak(boolean original) {
        if (original || !ControllerSupport.support().isCurrentInputMethodController()) {
            ControllerSupport.support().setCurrentInputMethod(false);
            return original;
        }
        var controller = ControllerSupport.support().manager().currentController().orElse(null);
        if (controller == null)
            return false;
        return ControllerSupport.support().mapping().find(MappingActions.BREAK)
                .flatMap(m -> m.getContextFor(controller))
                .isPresent();
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/lwjgl/input/Mouse;isButtonDown(I)Z",
                    ordinal = 1
            )
    )
    boolean controller$injectControllerInteract(boolean original) {
        if (original || !ControllerSupport.support().isCurrentInputMethodController()) {
            ControllerSupport.support().setCurrentInputMethod(false);
            return original;
        }
        var controller = ControllerSupport.support().manager().currentController().orElse(null);
        if (controller == null)
            return false;
        return ControllerSupport.support().mapping().find(MappingActions.INTERACT)
                .flatMap(m -> m.getContextFor(controller))
                .isPresent();
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;field_2778:Z"
            )
    )
    boolean controller$injectControllerAvailability(boolean original) {
        return original || ControllerSupport.support().isCurrentInputMethodController();
    }
    // end
}
