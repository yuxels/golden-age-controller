package cat.kittens.mods.controller.ui

public object ScrollableScreenGapComponent : ScrollableScreenComponent {
    override var context: ComponentContext? = null

    override fun render(mouseX: Int, mouseY: Int, delta: Float): Unit = Unit

    override fun isMouseOver(mouseX: Int, mouseY: Int): Boolean = false

    override val width: Int get() = 8

    override val height: Int get() = 8
}
