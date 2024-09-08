package cat.kittens.mods.controller.ui

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import java.awt.Color
import kotlin.math.ceil

public class ScrollableScreenSliderComponent : ScrollableScreenComponent {
    override var context: ComponentContext? = null

    public var min: Int = 0
    public var max: Int = 100
    public override var width: Int = 256
    public var baseTrackHeight: Int = 2
    public var message: String = ""
    public var trackColor: Color = Color(14737632)
    public var thumbColor: Color = Color(16777120)
    public var current: Int = min
    public var onChange: (ctx: ComponentContext, prev: Int, curr: Int) -> Unit = { _, _, _ -> }

    private val trackStartX: Int get() = 5 + MinecraftAccessor.instance().textRenderer.getWidth(message)
    private val trackEndX: Int get() = 5 + MinecraftAccessor.instance().textRenderer.getWidth("$current")

    public val fillerWidth: Int get() = trackStartX + trackEndX
    public val actualTrackWidth: Int get() = width - fillerWidth
    private val thumbX get() = trackStartX + (current * trackScale)
    public val trackScale: Int get() = ceil(actualTrackWidth.toDouble() / max).toInt()

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        val ctx = context ?: return
        val width = actualTrackWidth
        val thumbColor = if (isMouseOverThumb(mouseX, mouseY)) this.thumbColor else trackColor
        val trackY = ctx.coordinates.y + ((height - baseTrackHeight) / 2)
        var x = ctx.coordinates.x
        ctx.minecraft.textRenderer.draw(message, ctx.coordinates.x, trackY, trackColor.rgb, true)
        x += trackStartX
        fillGradient(
            x, trackY,
            x + width, trackY + baseTrackHeight,
            trackColor.rgb, trackColor.rgb
        )
        fillGradient(
            ctx.coordinates.x + thumbX, ctx.coordinates.y,
            ctx.coordinates.x + thumbX + trackScale, ctx.coordinates.y + height,
            thumbColor.rgb, thumbColor.rgb
        )
        x += width + 5
        ctx.minecraft.textRenderer.draw("$current", x, trackY, trackColor.rgb, true)
    }

    public fun isMouseOverThumb(mouseX: Int, mouseY: Int): Boolean {
        val ctx = context ?: return false
        return with(ctx.coordinates) {
            mouseX >= x + thumbX && mouseY >= y && mouseX <= x + thumbX + trackScale && mouseY <= y + height
        }
    }

    override fun handleClick(x: Int, y: Int, button: Int): Boolean {
        if (button != 0)
            return false
        val ctx = context ?: return false
        val baseX = x - ctx.coordinates.x - trackStartX
        if (baseX < 0 || baseX > actualTrackWidth)
            return false
        val curr = baseX - trackEndX
        if (curr > max || curr < min)
            return false
        val prev = current
        current = curr
        onChange(ctx, prev, current)
        return true
    }

    override val height: Int get() = baseTrackHeight + (baseTrackHeight * 1.5).toInt()
}
