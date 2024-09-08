package cat.kittens.mods.controller.mixin.accessor;

import net.minecraft.client.gui.widget.ButtonWidget;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ButtonWidget.class)
public interface ButtonWidgetAccessor {
    @Accessor("width")
    void width(int width);

    @Accessor("height")
    void height(int height);

    @Accessor("text")
    void text(String text);
}
