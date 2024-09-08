package cat.kittens.mods.controller.ui

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.ceil

public class ScrollableScreenTextComponent : ScrollableScreenComponent {
    override var context: ComponentContext? = null

    public var message: String = "Hello, world!"
    public var scale: Float = 1f
    public var layout: ScrollableScreenLayout = ScrollableScreenLayout()
    public var backgroundColor: Color? = Color(-1073741824)
    public var textColor: Color = Color(14737632)
    public var hoverColor: Color = Color(16777120)
    public var onClick: (ctx: ComponentContext, mouseX: Int, mouseY: Int) -> Unit = { _, _, _ -> }

    private val renderer get() = MinecraftAccessor.instance().textRenderer

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        val ctx = context ?: return
        if (backgroundColor != null) with(ctx.coordinates) {
            fillGradient(x, y, x + width, y + height, backgroundColor!!.rgb, backgroundColor!!.rgb)
        }
        val color = if (isMouseOver(mouseX, mouseY)) hoverColor else textColor
        GL11.glPushMatrix()
        GL11.glTranslatef(0f, 0f, 300f)
        renderer.drawWithShadow(
            message,
            ctx.coordinates.x + layout.leftPadding, ctx.coordinates.y + layout.topPadding,
            color.rgb
        )
        GL11.glPopMatrix()
    }

    override fun handleClick(x: Int, y: Int, button: Int): Boolean {
        val ctx = context ?: return false
        if (button == 0) {
            onClick(ctx, x, y)
            return true
        }
        return false
    }

    override val width: Int
        get() = ceil(
            ((renderer.getWidth(message) * scale) + layout.leftPadding
                    + layout.rightPadding).toDouble()
        ).toInt()

    override val height: Int
        get() = ceil(((FONT_SIZE * scale) + layout.topPadding + layout.bottomPadding).toDouble()).toInt()

    public companion object {
        public const val FONT_SIZE: Int = 7
    }
}
