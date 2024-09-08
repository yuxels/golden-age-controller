package cat.kittens.mods.controller.mixin.integration;

import cat.kittens.mods.controller.screen.ControllerConfigScreen;
import com.google.common.collect.ImmutableMap;
import com.llamalad7.mixinextras.sugar.Local;
import io.github.prospector.modmenu.ModMenu;
import net.minecraft.client.gui.screen.Screen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.function.Function;

@Mixin(ModMenu.class)
public class ModMenuMixin {
    @Inject(
            method = "onInitializeClient",
            at = @At(
                    target = "Lcom/google/common/collect/ImmutableMap$Builder;put(Ljava/lang/Object;Ljava/lang/Object;)Lcom/google/common/collect/ImmutableMap$Builder;",
                    value = "INVOKE"
            ),
            remap = false
    )
    void controller$addModMenuScreen(
            CallbackInfo callback, @Local ImmutableMap.Builder<String, Function<Screen, ? extends Screen>> factories
    ) {
        factories.put("controller", (parent) -> ControllerConfigScreen.screen());
    }
}
