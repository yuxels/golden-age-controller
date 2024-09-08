package cat.kittens.mods.controller.ui

import cat.kittens.mods.controller.util.GLUtils
import java.awt.Color
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.max

public data class ScrollableScreenComponentColumn(
    private val top: List<ScrollableScreenComponent>,
    private val bottom: List<ScrollableScreenComponent>,
    private val layout: ScrollableScreenLayout,
    private val itemSpacing: Int,
    private val hasBackground: Boolean
) : ScrollableScreenComponent {
    override var context: ComponentContext? = null
        set(ctx) {
            field = ctx!!
            val minY = ctx.coordinates.y
            val maxY = minY + height
            val currentX = ctx.coordinates.x
            var currentY =
                ceil((ctx.coordinates.y + ((ctx.scrollBarWidget?.progress ?: 0f) * (minY - maxY))).toDouble())
                    .toInt()
            for (component in this.top) {
                val height = component.height
                component.context = ctx.copy(
                    coordinates = ctx.coordinates.copy(
                        x = currentX + layout.leftPadding,
                        y = currentY + layout.topPadding,
                        x2 = ctx.coordinates.x2 - layout.leftPadding,
                        y2 = currentY + height
                    )
                )
                currentY += height + itemSpacing
            }
            currentY +=
                computeDistanceBetweenSections(currentY - ctx.coordinates.y - itemSpacing, getHeight(bottom))
            for (component in this.bottom) {
                val height = component.height
                component.context = ctx.copy(
                    coordinates = ctx.coordinates.copy(
                        x = currentX + layout.leftPadding,
                        y = currentY + layout.topPadding,
                        x2 = ctx.coordinates.x2 - layout.leftPadding,
                        y2 = currentY + height
                    )
                )
                currentY += height + itemSpacing
            }
        }

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        val ctx = context ?: return
        if (hasBackground)
            fillGradient(
                ctx.coordinates.x, ctx.coordinates.y, ctx.coordinates.x2, ctx.coordinates.y2, BG_COLOR.rgb, BG_COLOR.rgb
            )
        GLUtils.scissor(
            ctx.coordinates.x + layout.leftPadding, ctx.coordinates.x2 - layout.rightPadding,
            ctx.coordinates.y + layout.topPadding, ctx.coordinates.y2 - layout.bottomPadding
        ) {
            top.forEach { it.render(mouseX, mouseY, delta) }
            bottom.forEach { it.render(mouseX, mouseY, delta) }
        }
        ctx.scrollBarWidget?.render(delta)
    }

    public val scrollBarWidth: Int = 6

    override fun isMouseOver(mouseX: Int, mouseY: Int): Boolean {
        val ctx = context ?: return false
        return (mouseX >= ctx.coordinates.x && mouseX < ctx.coordinates.x2 && mouseY >= ctx.coordinates.y &&
                mouseY <= ctx.coordinates.y2) || (ctx.scrollBarWidget?.isMouseOver(mouseX, mouseY) ?: false)
    }

    private fun computeDistanceBetweenSections(
        topHeight: Int,
        bottomHeight: Int
    ): Int {
        val ctx = context?.renderContext ?: return itemSpacing
        val overallHeight = ctx.bottom - ctx.top - layout.topPadding - layout.bottomPadding
        return max(
            itemSpacing.toDouble(),
            overallHeight - abs((topHeight - bottomHeight).toDouble())
        ).toInt()
    }

    private fun getHeight(components: List<ScrollableScreenComponent>): Int {
        if (context == null)
            return itemSpacing
        var totalHeight = 0
        for (component in components) {
            totalHeight += component.height
        }
        return totalHeight + (itemSpacing * (components.size - 1))
    }

    override val width: Int
        get() {
            var max = 0
            for (component in top) {
                if (component.width > max) max = component.width
            }
            for (component in bottom) {
                if (component.width > max) max = component.width
            }
            return max + layout.leftPadding + layout.rightPadding
        }

    override val height: Int
        get() {
            val top = getHeight(this.top)
            val bottom = getHeight(this.bottom)
            return top + bottom + computeDistanceBetweenSections(top, bottom)
        }

    override fun handleClick(x: Int, y: Int, button: Int): Boolean {
        val ctx = context ?: return false
        fun handle(c: ScrollableScreenComponent) {
            if (!c.isMouseOver(x, y) && !c.shouldBypassClickPositionCheck(x, y))
                return
            c.handleClick(x, y, button)
        }
        top.forEach(::handle)
        bottom.forEach(::handle)
        if (isMouseOver(x, y) && ctx.scrollBarWidget != null) {
            ctx.scrollBarWidget?.onClick(x, y, button)
        }
        return false
    }

    override fun handleScroll(x: Int, y: Int, amount: Int): Boolean {
        val ctx = context ?: return false
        for (c in top + bottom) {
            if (!c.isMouseOver(x, y)) continue
            if (c.handleScroll(x, y, amount)) return true
        }
        return isMouseOver(x, y) && ctx.scrollBarWidget != null && ctx.scrollBarWidget?.onScroll(amount) != null
    }

    public companion object {
        public val BG_COLOR: Color = Color(0f, 0f, 0f, 0.5f)
    }
}
