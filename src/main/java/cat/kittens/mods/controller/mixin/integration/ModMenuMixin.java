package cat.kittens.mods.controller.mixin.integration;

import cat.kittens.mods.controller.screen.ControllerConfigScreen;
import com.google.common.collect.ImmutableMap;
import io.github.prospector.modmenu.ModMenu;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.function.Function;

@Mixin(value = ModMenu.class, remap = false)
public class ModMenuMixin {
    @Inject(
            method = "onInitializeClient",
            at = @At(
                    target = "Lcom/google/common/collect/ImmutableMap$Builder;build()Lcom/google/common/collect/ImmutableMap;",
                    value = "INVOKE",
                    shift = At.Shift.BEFORE
            ),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    void controller$addModMenuScreen(
            CallbackInfo ci,  ImmutableMap.Builder<String, Function<Screen, ? extends Screen>> factories
    ) {
        factories.put("controller", (parent) -> ControllerConfigScreen.screen());
    }
}
