package cat.kittens.mods.controller.util

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import org.lwjgl.opengl.GL11

public object GLUtils {
    public fun scissor(x: Int, x2: Int, y: Int, y2: Int, callback: () -> Unit) {
        val mc = MinecraftAccessor.instance()
        val factor = ScaledResolution(mc.options, mc.displayWidth, mc.displayHeight)
        GL11.glScissor(
            x * factor.guiScale, ((factor.guiScaledHeight - y2) * factor.guiScale).toInt(),
            (x2 - x) * factor.guiScale, (y2 - y) * factor.guiScale
        )
        GL11.glEnable(GL11.GL_SCISSOR_TEST)
        callback()
        GL11.glDisable(GL11.GL_SCISSOR_TEST)
    }
}
