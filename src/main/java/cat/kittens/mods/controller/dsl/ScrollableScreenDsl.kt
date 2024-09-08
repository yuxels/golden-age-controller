package cat.kittens.mods.controller.dsl

import cat.kittens.mods.controller.ui.*

@DslMarker
public annotation class ScreenDslMarker

@ScreenDslMarker
public data class ScreenLayoutDsl(
    var topPadding: Int, var bottomPadding: Int, var leftPadding: Int, var rightPadding: Int
) {
    public inline var x: Int
        get() = (leftPadding + rightPadding) / 2
        set(value) {
            leftPadding = value
            rightPadding = value
        }

    public inline var y: Int
        get() = (topPadding + bottomPadding) / 2
        set(value) {
            topPadding = value
            bottomPadding = value
        }

    @ScreenDslMarker
    public operator fun invoke(block: ScreenLayoutDsl.() -> Unit): Unit = run(block)

    public fun build(): ScrollableScreenLayout =
        ScrollableScreenLayout(topPadding, leftPadding, bottomPadding, rightPadding)
}


@ScreenDslMarker
public data class ScreenColumnComponentDsl(
    var componentLayout: ScreenLayoutDsl = ScreenLayoutDsl(0, 0, 0, 0),
    var itemSpacing: Int = 8,
    var hasBackground: Boolean = true,
    var _top: MutableList<ScrollableScreenComponent> = mutableListOf(),
    var _bottom: MutableList<ScrollableScreenComponent> = mutableListOf()
) {
    public enum class Position {
        Top, Bottom
    }

    @ScreenDslMarker
    public operator fun invoke(block: ScreenColumnComponentDsl.() -> Unit): Unit = run(block)

    public inline val top: Position get() = Position.Top
    public inline val bottom: Position get() = Position.Bottom

    public operator fun Position.plus(component: ScrollableScreenComponent) {
        when (this) {
            Position.Top -> _top.add(component)
            Position.Bottom -> _bottom.add(component)
        }
    }

    @ScreenDslMarker
    public fun button(block: ScrollableScreenButtonComponent.() -> Unit = {}): ScrollableScreenButtonComponent =
        ScrollableScreenComponent.button().apply(block)

    @ScreenDslMarker
    public fun text(block: ScrollableScreenTextComponent.() -> Unit = {}): ScrollableScreenTextComponent =
        ScrollableScreenComponent.text().apply(block)

    @ScreenDslMarker
    public fun text(
        message: String,
        block: ScrollableScreenTextComponent.() -> Unit = {}
    ): ScrollableScreenTextComponent =
        ScrollableScreenComponent.text().apply {
            this.message = message
            block()
        }

    @ScreenDslMarker
    public fun gap(block: ScrollableScreenGapComponent.() -> Unit = {}): ScrollableScreenGapComponent =
        ScrollableScreenComponent.gap().apply(block)

    @ScreenDslMarker
    public fun dropdown(block: ScrollableScreenDropdownComponent.() -> Unit = {}): ScrollableScreenDropdownComponent =
        ScrollableScreenComponent.dropdown().apply(block)

    @ScreenDslMarker
    public fun slider(block: ScrollableScreenSliderComponent.() -> Unit = {}): ScrollableScreenSliderComponent =
        ScrollableScreenComponent.slider().apply(block)

    public fun build(): ScrollableScreenComponentColumn =
        ScrollableScreenComponentColumn(_top, _bottom, componentLayout.build(), itemSpacing, hasBackground)
}

@ScreenDslMarker
public data class ScrollableScreenDsl(
    public var title: String = "Untitled",
    public var layout: ScreenLayoutDsl = ScreenLayoutDsl(0, 0, 0, 0),
    public var left: ScreenColumnComponentDsl = ScreenColumnComponentDsl(),
    public var right: ScreenColumnComponentDsl = ScreenColumnComponentDsl()
) {
    @ScreenDslMarker
    public operator fun invoke(block: ScrollableScreenDsl.() -> Unit): ScrollableScreenDsl = apply(block)

    public fun build(): ScrollableScreen = ScrollableScreen(layout.build(), title, { left.build() }, { right.build() })
}

@ScreenDslMarker
public fun scrollableScreen(
    title: String = "Untitled", block: ScrollableScreenDsl.() -> Unit
): ScrollableScreenDsl = ScrollableScreenDsl(title).apply(block)
