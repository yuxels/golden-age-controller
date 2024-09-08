package cat.kittens.mods.controller.ui

import net.minecraft.client.render.Tessellator
import org.lwjgl.opengl.GL11
import kotlin.math.ceil
import kotlin.math.max
import kotlin.math.min

public data class ScrollBarWidget(
    private val x: Int,
    private val y: Int,
    private val width: Int,
    private val height: Int,
    private val pageHeight: Int,
    private val top: Int
) {
    private var targetScrollPercent = 0f
    private var scrollPercent = 0f

    public var shouldRender: () -> Boolean = { pageHeight > height }

    private fun drawRectangle(t: Tessellator, x: Int, y: Int, width: Int, height: Int) {
        t.vertex(x.toDouble(), y.toDouble(), 0.0)
        t.vertex(x.toDouble(), (y + height).toDouble(), 0.0)
        t.vertex((x + width).toDouble(), (y + height).toDouble(), 0.0)
        t.vertex((x + width).toDouble(), y.toDouble(), 0.0)
    }

    public val gripY: Int
        get() {
            val availableTrackY = (height - gripHeight).toFloat()
            return ceil((top + (availableTrackY * scrollPercent)).toDouble()).toInt()
        }

    public val gripHeight: Int
        get() {
            val scrollableHeight = max((pageHeight - height).toDouble(), 0.0).toInt()
            val idealGripHeight = ceil(height.toDouble() * (height / scrollableHeight.toFloat())).toInt()
            return min(idealGripHeight.toDouble(), height.toDouble()).toInt()
        }

    public val progress: Float
        get() =
            if (shouldRender()) scrollPercent else 0f

    public fun render(delta: Float) {
        if (!shouldRender()) return
        update(delta)
        GL11.glDisable(GL11.GL_TEXTURE_2D)
        val tessellator = Tessellator.INSTANCE
        tessellator.startQuads()
        tessellator.color(8421504, 255)
        drawRectangle(tessellator, x, y, width, height)
        tessellator.color(12632256, 255)
        drawRectangle(tessellator, x, gripY, width, gripHeight)
        tessellator.draw()
        GL11.glEnable(GL11.GL_TEXTURE_2D)
    }

    private fun update(delta: Float) {
        scrollPercent += (targetScrollPercent - scrollPercent) * SMOOTH_SCROLLING_FACTOR * delta
        scrollPercent = min(1.0, max(0.0, scrollPercent.toDouble())).toFloat()
    }

    public fun isMouseOver(mouseX: Int, mouseY: Int): Boolean {
        return mouseX >= x && mouseX < (x + width) && mouseY >= y && mouseY <= (y + height)
    }

    public fun onClick(mouseX: Int, mouseY: Int, button: Int) {
        if (!shouldRender() || button != 0 || !isMouseOver(mouseX, mouseY)) return
        val y = max(0.0, (mouseY - this.y).toDouble()).toFloat()
        val ratio = y / height
        this.targetScrollPercent = max(0.0, min(1.0, ratio.toDouble())).toFloat()
    }

    public fun onScroll(amount: Int) {
        this.targetScrollPercent = max(0.0, min(1.0, targetScrollPercent + amount.toDouble() / 8)).toFloat()
    }

    private companion object {
        private const val SMOOTH_SCROLLING_FACTOR = 0.1f
    }
}
