package cat.kittens.mods.controller.mixin;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.input.MappingActions;
import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    // Prevent LWJGL2's controller API to be initialized, since we are going to use SDL3 for this.
    @Redirect(
            method = "init",
            at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Controllers;create()V")
    )
    void controller$preventLWJGL2ControllerInit() {
    }

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
        if (original || !ControllerSupport.INSTANCE.isControllerActive()) {
            return original;
        }
        var controller = ControllerSupport.INSTANCE.getManager().getCurrentController();
        if (controller == null)
            return false;
        var action = ControllerSupport.INSTANCE.getMappingView().get(MappingActions.BREAK);
        return action != null && action.getContextFor(controller) != null;
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
        if (original || !ControllerSupport.INSTANCE.isControllerActive()) {
            ControllerSupport.INSTANCE.setControllerActive(false);
            return original;
        }
        var controller = ControllerSupport.INSTANCE.getManager().getCurrentController();
        if (controller == null)
            return false;
        var action = ControllerSupport.INSTANCE.getMappingView().get(MappingActions.BREAK);
        return action != null && action.getContextFor(controller) != null;
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
        if (original || !ControllerSupport.INSTANCE.isControllerActive()) {
            ControllerSupport.INSTANCE.setControllerActive(false);
            return original;
        }
        var controller = ControllerSupport.INSTANCE.getManager().getCurrentController();
        if (controller == null)
            return false;
        var action = ControllerSupport.INSTANCE.getMappingView().get(MappingActions.INTERACT);
        return action != null && action.getContextFor(controller) != null;
    }

    @ModifyExpressionValue(
            method = "tick",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/client/Minecraft;field_2778:Z"
            )
    )
    boolean controller$injectControllerAvailability(boolean original) {
        return original || ControllerSupport.INSTANCE.isControllerActive();
    }
    // end
}
