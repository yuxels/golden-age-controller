package cat.kittens.mods.controller.input.processing

import cat.kittens.mods.controller.ControllerSupport
import cat.kittens.mods.controller.input.MappingActions
import cat.kittens.mods.controller.input.value
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import kotlin.math.*

public class ControllerAimingHandler {
    private var aimX = 0.0
    private var aimY = 0.0

    public fun tick(delta: Float) {
        if (MinecraftAccessor.instance().currentScreen != null || MinecraftAccessor.instance().player == null ||
            ControllerSupport.manager.currentController == null
        ) return
        processInput()
        externalize(delta)
    }

    private fun processInput() {
        val x: Double = (ControllerSupport.mappingView[MappingActions.AIM_RIGHT]?.value ?: 0.0) -
                (ControllerSupport.mappingView[MappingActions.AIM_LEFT]?.value ?: 0.0)
        val y: Double = (ControllerSupport.mappingView[MappingActions.AIM_UP]?.value ?: 0.0) -
                (ControllerSupport.mappingView[MappingActions.AIM_DOWN]?.value ?: 0.0)
        val length = abs(sqrt((x * x) + (y * y)))
        val angle = atan2(y, x)
        aimX = (cos(angle) * length) * ControllerSupport.config.rightStickXSensitivity
        aimY = (sin(angle) * length) * ControllerSupport.config.rightStickYSensitivity
    }

    private fun externalize(delta: Float) = MinecraftAccessor.instance().player
        .changeLookDirection(aimX.toFloat() * delta, aimY.toFloat() * delta)
}
