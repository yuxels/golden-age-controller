package cat.kittens.mods.controller.ui

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import net.minecraft.client.Minecraft
import net.minecraft.client.render.Tessellator
import org.lwjgl.opengl.GL11

public data class ComponentCoordinates(
    val x: Int, val y: Int, val x2: Int, val y2: Int
)

public data class ComponentContext(
    var coordinates: ComponentCoordinates,
    var renderContext: ScrollableScreenRenderContext,
    var screen: ScrollableScreen,
    var scrollBarWidget: ScrollBarWidget?
) {
    inline val minecraft: Minecraft get() = MinecraftAccessor.instance()

    public constructor(screen: ScrollableScreen, ctx: ScrollableScreenRenderContext, x: Int, y: Int, x2: Int, y2: Int) :
            this(ComponentCoordinates(x, y, x2, y2), ctx, screen, null)

    public companion object {
        public operator fun invoke(
            screen: ScrollableScreen, ctx: ScrollableScreenRenderContext,
            x: Int, y: Int, x2: Int, y2: Int, component: ScrollableScreenComponentColumn? = null
        ): ComponentContext = ComponentContext(screen, ctx, x, y, x2, y2).apply {
            if (component != null)
                scrollBarWidget = ScrollBarWidget(x2, y, component.scrollBarWidth, y2 - y, component.height, y)
        }
    }
}

public interface ScrollableScreenComponent {
    public var context: ComponentContext?

    public fun shouldBypassClickPositionCheck(mouseX: Int, mouseY: Int): Boolean {
        return false
    }

    public fun render(mouseX: Int, mouseY: Int, delta: Float)

    public val width: Int

    public val height: Int

    public fun handleClick(x: Int, y: Int, button: Int): Boolean = false

    public fun handleScroll(x: Int, y: Int, amount: Int): Boolean = false

    public fun isMouseOver(mouseX: Int, mouseY: Int): Boolean = context?.coordinates?.run {
        mouseX >= x && mouseY >= y && mouseX <= x + width && mouseY <= y + height
    } ?: false

    public companion object {
        public fun text(): ScrollableScreenTextComponent =
            ScrollableScreenTextComponent()

        public fun button(): ScrollableScreenButtonComponent =
            ScrollableScreenButtonComponent()

        public fun gap(): ScrollableScreenGapComponent = ScrollableScreenGapComponent

        public fun dropdown(): ScrollableScreenDropdownComponent =
            ScrollableScreenDropdownComponent()

        public fun slider(): ScrollableScreenSliderComponent =
            ScrollableScreenSliderComponent()
    }
}

public fun ScrollableScreenComponent.doRender(mouseX: Int, mouseY: Int, delta: Float) {
    GL11.glPushMatrix()
    render(mouseX, mouseY, delta)
    GL11.glPopMatrix()
}

internal fun ScrollableScreenComponent.fillGradient(
    startX: Int, startY: Int, endX: Int, endY: Int, colorStart: Int, colorEnd: Int
) {
    val var7 = (colorStart shr 24 and 255) / 255.0f
    val var8 = (colorStart shr 16 and 255) / 255.0f
    val var9 = (colorStart shr 8 and 255) / 255.0f
    val var10 = (colorStart and 255) / 255.0f
    val var11 = (colorEnd shr 24 and 255) / 255.0f
    val var12 = (colorEnd shr 16 and 255) / 255.0f
    val var13 = (colorEnd shr 8 and 255) / 255.0f
    val var14 = (colorEnd and 255) / 255.0f
    GL11.glDisable(3553)
    GL11.glEnable(3042)
    GL11.glDisable(3008)
    GL11.glBlendFunc(770, 771)
    GL11.glShadeModel(7425)
    val tessellator = Tessellator.INSTANCE
    tessellator.startQuads()
    tessellator.color(var8, var9, var10, var7)
    tessellator.vertex(endX.toDouble(), startY.toDouble(), 0.0)
    tessellator.vertex(startX.toDouble(), startY.toDouble(), 0.0)
    tessellator.color(var12, var13, var14, var11)
    tessellator.vertex(startX.toDouble(), endY.toDouble(), 0.0)
    tessellator.vertex(endX.toDouble(), endY.toDouble(), 0.0)
    tessellator.draw()
    GL11.glShadeModel(7424)
    GL11.glDisable(3042)
    GL11.glEnable(3008)
    GL11.glEnable(3553)
}
