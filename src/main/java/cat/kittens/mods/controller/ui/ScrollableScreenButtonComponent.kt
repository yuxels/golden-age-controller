package cat.kittens.mods.controller.ui

import cat.kittens.mods.controller.mixin.accessor.ButtonWidgetAccessor
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import net.minecraft.client.gui.widget.ButtonWidget
import java.awt.Color

public class ScrollableScreenButtonComponent : ScrollableScreenComponent {
    override var context: ComponentContext? = null

    public var message: String = "Hello, world!"
    public var textColor: Color = Color(14737632)
    public override var width: Int = 300
    public override var height: Int = 20
    public var onClick: (ctx: ComponentContext, mouseX: Int, mouseY: Int) -> Unit = { _, _, _ -> }

    private lateinit var widget: ButtonWidget

    public fun dimensions(width: Int, height: Int): ScrollableScreenButtonComponent {
        this.width = width
        this.height = height
        return this
    }

    public fun fitDimensions(height: Int): ScrollableScreenButtonComponent {
        this.width = MinecraftAccessor.instance().textRenderer.getWidth(message) + 20
        this.height = height
        return this
    }

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        val ctx = context ?: return
        if (!::widget.isInitialized)
            this.widget = ButtonWidget(10000, ctx.coordinates.x, ctx.coordinates.y, width, height, message)
        widget.x = ctx.coordinates.x
        widget.y = ctx.coordinates.y
        (widget as ButtonWidgetAccessor).let {
            it.width(width)
            it.height(height)
            it.text(message)
        }
        widget.render(MinecraftAccessor.instance(), mouseX, mouseY)
    }

    override fun isMouseOver(mouseX: Int, mouseY: Int): Boolean =
        widget.isMouseOver(MinecraftAccessor.instance(), mouseX, mouseY)

    override fun handleClick(x: Int, y: Int, button: Int): Boolean {
        if (button == 0) {
            onClick(context ?: return false, x, y)
            return true
        }
        return false
    }
}
