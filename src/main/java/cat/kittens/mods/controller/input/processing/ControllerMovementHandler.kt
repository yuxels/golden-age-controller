package cat.kittens.mods.controller.input.processing

import cat.kittens.mods.controller.ControllerSupport
import cat.kittens.mods.controller.input.MappingActions
import cat.kittens.mods.controller.input.value
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import kotlin.math.min

public class ControllerMovementHandler {
    private var movementForward = 0.0
    private var movementLeftward = 0.0
    private var isJumping = false
    private var isSneaking = false

    public fun tick() {
        if (MinecraftAccessor.instance().currentScreen != null || MinecraftAccessor.instance().player == null ||
            ControllerSupport.manager.currentController == null
        ) return
        processInput()
        externalize()
    }

    private fun processInput() {
        movementForward = (ControllerSupport.mappingView[MappingActions.WALK_FORWARD]?.value ?: 0.0) -
                (ControllerSupport.mappingView[MappingActions.WALK_BACKWARD]?.value ?: 0.0)
        movementLeftward = (ControllerSupport.mappingView[MappingActions.WALK_LEFTWARD]?.value ?: 0.0) -
                (ControllerSupport.mappingView[MappingActions.WALK_RIGHTWARD]?.value ?: 0.0)
        isJumping = ControllerSupport.mappingView[MappingActions.JUMP]?.value != null
        isSneaking = ControllerSupport.mappingView[MappingActions.SNEAK]?.value != null
    }

    private fun externalize() {
        MinecraftAccessor.instance().player.field_161.field_2533 =
            min(1.0, MinecraftAccessor.instance().player.field_161.field_2533 + movementForward).toFloat()
        MinecraftAccessor.instance().player.field_161.field_2532 =
            min(1.0, MinecraftAccessor.instance().player.field_161.field_2532 + movementLeftward).toFloat()
        MinecraftAccessor.instance().player.field_161.field_2536 = isSneaking ||
                MinecraftAccessor.instance().player.field_161.field_2536
        MinecraftAccessor.instance().player.field_161.field_2535 = isJumping ||
                MinecraftAccessor.instance().player.field_161.field_2535
    }
}
