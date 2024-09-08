package cat.kittens.mods.controller.ui

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import net.minecraft.client.gui.screen.Screen
import org.lwjgl.input.Mouse
import kotlin.math.sign


public class ScrollableScreen(
    private val layout: ScrollableScreenLayout,
    title: String = "Untitled",
    supplyLeft: () -> ScrollableScreenComponentColumn? = { null },
    supplyRight: () -> ScrollableScreenComponentColumn? = { null }
) : Screen() {
    public var left: ScrollableScreenComponentColumn? = null

    public var right: ScrollableScreenComponentColumn? = null

    public var title: ScrollableScreenComponent =
        ScrollableScreenComponent.text().apply { message = title; backgroundColor = null }
        set(value) {
            field = value
            init()
        }

    public var supplyLeft: () -> ScrollableScreenComponentColumn? = supplyLeft
        set(value) {
            field = value
            init()
        }

    public var supplyRight: () -> ScrollableScreenComponentColumn? = supplyRight
        set(value) {
            field = value
            init()
        }

    private val lastScreen: Screen =
        MinecraftAccessor.instance().currentScreen

    override fun init() {
        left = supplyLeft()
        right = supplyRight()
        val renderCtx = ScrollableScreenRenderContext(
            width, height, layout.topPadding + (2 * layout.topPadding), height - layout.bottomPadding,
            lastScreen
        )
        val titleX = (width / 2) - (title.width / 2)
        val titleY = layout.topPadding
        val titleX2 = width / 2 + (title.width / 2)
        val titleY2 = titleY + title.height
        title.context = ComponentContext(this, renderCtx, titleX, titleY, titleX2, titleY2)
        val leftX = layout.leftPadding
        val leftY = titleY + (2 * layout.topPadding)
        var leftX2 = leftX + (left?.width ?: 0)
        val leftY2 = height - layout.bottomPadding
        if (right != null) {
            val rightX = leftX2 + layout.leftPadding + (left?.scrollBarWidth ?: 0)
            val rightY = leftY
            val rightX2 = width - layout.rightPadding - right!!.scrollBarWidth
            val rightY2 = leftY2
            right!!.context = ComponentContext(this, renderCtx, rightX, rightY, rightX2, rightY2, right)
        } else {
            leftX2 = width - leftX
        }
        left?.context = left?.let {
            ComponentContext(this, renderCtx, leftX, leftY, leftX2, leftY2, it)
        }
    }

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground()
        title.doRender(mouseX, mouseY, delta)
        left?.doRender(mouseX, mouseY, delta)
        right?.doRender(mouseX, mouseY, delta)
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, button: Int) {
        left?.handleClick(mouseX, mouseY, button)
        right?.handleClick(mouseX, mouseY, button)
    }

    public fun handleScroll(x: Int, y: Int) {
        if (left == null && right == null)
            return
        val wheel = Mouse.getEventDWheel()
        if (wheel == 0) return
        val amount = -sign(wheel.toDouble()).toInt()
        left?.handleScroll(x, y, amount)
        right?.handleScroll(x, y, amount)
    }
}
