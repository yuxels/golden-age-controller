package cat.kittens.mods.controller.mixin;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.input.ActionIds;
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.Minecraft;
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
        ControllerSupport.support().manager().tryLibInit();
    }
    // end

    // Tick controller inputs.
    @Inject(
            method = "run",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/lang/System;nanoTime()J",
                    ordinal = 0,
                    shift = At.Shift.AFTER
            ),
            remap = false
    )
    void controller$tickOffTick(CallbackInfo ignored) {
        ControllerSupport.support().manager().tick();
        ControllerSupport.support().manager().currentController().ifPresent(controller -> {
            if (controller.chord().performAllOffTick(ControllerSupport.support().mapping())) {
                ControllerSupport.support().setCurrentInputMethod(true);
            }
        });
        ControllerSupport.support().aiming().tick();
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;method_2110(IZ)V",
                    shift = At.Shift.AFTER
            )
    )
    void controller$tick(CallbackInfo callback) {
        ControllerSupport.support().manager().currentController().ifPresent(controller -> {
            MinecraftAccessor mc = (MinecraftAccessor) this;
            if (mc.lastInteraction() > mc.currentTicks()) {
                mc.setLastInteraction(mc.currentTicks());
            }
            if (controller.chord().performAll(ControllerSupport.support().mapping())) {
                ControllerSupport.support().setCurrentInputMethod(true);
            }
        });
    }

    @Redirect(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;method_2110(IZ)V"
            )
    )
    void controller$preventControllerConflicts$1(Minecraft instance, int bl, boolean b) {
        if (!ControllerSupport.support().isCurrentInputMethodController()) {
            ((MinecraftAccessor) instance).holdInteract(bl, b);
            return;
        }
        ControllerSupport.support().manager().currentController().ifPresent(controller -> {
            boolean active = controller.chord()
                    .shouldPerform(ControllerSupport.support().mapping().getActions().get(ActionIds.BREAK));
            ((MinecraftAccessor) instance).holdInteract(bl, active);
        });
    }
    // end

    // Bring back keyboard & mouse input method.
    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/entity/player/ClientPlayerEntity;method_136(IZ)V",
                    shift = At.Shift.BEFORE
            )
    )
    void controller$keyboard$setCurrentInputMethod(CallbackInfo ci) {
        ControllerSupport.support().setCurrentInputMethod(false);
    }

    @Inject(
            method = "tick",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/Minecraft;method_2107(I)V",
                    shift = At.Shift.BEFORE
            )
    )
    void controller$mouse$setCurrentInputMethod(CallbackInfo ci) {
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
    void controller$mouse$setCurrentInputMethod2(CallbackInfo ci) {
        ControllerSupport.support().setCurrentInputMethod(false);
    }
    // end
}
