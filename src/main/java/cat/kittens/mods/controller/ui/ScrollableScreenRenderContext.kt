package cat.kittens.mods.controller.ui

import net.minecraft.client.gui.screen.Screen

public data class ScrollableScreenRenderContext(
    var width: Int,
    var height: Int,
    var top: Int,
    var bottom: Int,
    var previousScreen: Screen?
)
