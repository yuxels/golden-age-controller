package cat.kittens.mods.controller.ui

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import org.lwjgl.opengl.GL11
import java.awt.Color
import kotlin.math.ceil
import kotlin.math.max

public class ScrollableScreenDropdownComponent : ScrollableScreenComponent {
    override var context: ComponentContext? = null

    public var selectedValue: String = "Hello, world!"
    public var scale: Float = 1f
    public var layout: ScrollableScreenLayout = ScrollableScreenLayout(5, 10, 5, 10)
    public var backgroundColor: Color? = Color(75, 75, 75, 128)
    public var textColor: Color = Color(224, 224, 224)
    public var hoverColor: Color = Color(255, 255, 160)
    public var outlineColor: Color = Color(75, 75, 75)
    public var options: MutableList<String> = mutableListOf()
    public var onSelect: (ctx: ComponentContext, option: String) -> Unit = { _, _ -> }
    public var isExpanded: Boolean = false

    override fun shouldBypassClickPositionCheck(mouseX: Int, mouseY: Int): Boolean = true

    private val renderer get() = MinecraftAccessor.instance().textRenderer

    override fun render(mouseX: Int, mouseY: Int, delta: Float) {
        val ctx = context ?: return
        val width = width - (STROKE_SIZE * 2)
        val height = baseHeight - (STROKE_SIZE * 2)
        val outlineColor =
            (if (isMouseOverDropdownOnly(mouseX, mouseY)) this.hoverColor else this.outlineColor).rgb
        val textColor = (if (isMouseOverDropdownOnly(mouseX, mouseY)) this.hoverColor else this.textColor).rgb
        if (backgroundColor != null)
            with(ctx.coordinates) {
                fillGradient(x, y, x + width, y + STROKE_SIZE, outlineColor, outlineColor)
                fillGradient(x, y + height - STROKE_SIZE, x + width, y + height, outlineColor, outlineColor)
                fillGradient(x, y + STROKE_SIZE, x + STROKE_SIZE, y + height - STROKE_SIZE, outlineColor, outlineColor)
                fillGradient(
                    x + width - STROKE_SIZE,
                    y + STROKE_SIZE,
                    x + width,
                    y + height - STROKE_SIZE,
                    outlineColor,
                    outlineColor
                )
                fillGradient(
                    x + STROKE_SIZE,
                    y + STROKE_SIZE,
                    x + width - STROKE_SIZE,
                    y + height - STROKE_SIZE,
                    backgroundColor!!.rgb,
                    backgroundColor!!.rgb
                )
            }
        renderer.drawWithShadow(
            "$selectedValue ⌄", ctx.coordinates.x + layout.leftPadding,
            ctx.coordinates.y + layout.topPadding, textColor
        )
        if (isExpanded) {
            var y2 = ctx.coordinates.y + height + (STROKE_SIZE * 2)
            val x2 = ctx.coordinates.x
            for (option in options) {
                if (backgroundColor != null) {
                    fillGradient(
                        x2,
                        y2,
                        ctx.coordinates.x + width,
                        y2 + height,
                        backgroundColor!!.rgb,
                        backgroundColor!!.rgb
                    )
                }
                GL11.glPushMatrix()
                GL11.glTranslatef(0f, 0f, 300f)
                renderer.drawWithShadow(option, x2 + layout.leftPadding, y2 + layout.topPadding, textColor)
                GL11.glPopMatrix()
                y2 += height
            }
        }
    }

    public fun isMouseOverDropdownOnly(mouseX: Int, mouseY: Int): Boolean {
        val ctx = context ?: return false
        return mouseX >= ctx.coordinates.x && mouseY >= ctx.coordinates.y && mouseX <= ctx.coordinates.x + width &&
                mouseY <= ctx.coordinates.y + baseHeight
    }

    override fun handleClick(x: Int, y: Int, button: Int): Boolean {
        if (button != 0 || options.isEmpty())
            return false
        val ctx = context ?: return false
        if (isMouseOverDropdownOnly(x, y)) {
            this.isExpanded = !isExpanded
            return true
        } else if (isExpanded && isMouseOver(x, y)) {
            val baseWidth = width
            val baseHeight = baseHeight - (STROKE_SIZE * 2)
            val baseX = ctx.coordinates.x
            var baseY = ctx.coordinates.y + baseHeight + (STROKE_SIZE * 2)
            for (option in options) {
                val isMouseOver =
                    x >= baseX && y >= baseY && x <= baseX + baseWidth && y <= baseY + baseHeight
                if (isMouseOver) {
                    isExpanded = false
                    selectedValue = option
                    onSelect(ctx, option)
                    return true
                }
                baseY += baseHeight
            }
        }
        return false
    }

    override val width: Int
        get() {
            val baseWidth = max(
                renderer.getWidth(selectedValue).toDouble(),
                (options.maxOfOrNull { text -> renderer.getWidth(text) } ?: 0).toDouble()
            )
            return (STROKE_SIZE * 2) + ceil(
                ((baseWidth * scale) + layout.leftPadding + layout.rightPadding)
            ).toInt()
        }

    public val baseHeight: Int
        get() =
            (STROKE_SIZE * 2) + ceil(((FONT_SIZE * scale) + layout.topPadding + layout.bottomPadding).toDouble())
                .toInt()

    override val height: Int get() = baseHeight + ((baseHeight + STROKE_SIZE * 2) * options.size)

    private companion object {
        private inline val FONT_SIZE get() = ScrollableScreenTextComponent.FONT_SIZE
        private const val STROKE_SIZE = 1
    }
}
