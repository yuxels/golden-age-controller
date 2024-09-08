package cat.kittens.mods.controller.input.processing

import cat.kittens.mods.controller.ControllerSupport
import cat.kittens.mods.controller.input.MappingActionContext
import cat.kittens.mods.controller.input.MappingActions
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor
import kotlinx.coroutines.*
import net.minecraft.client.Minecraft
import org.lwjgl.input.Keyboard

private suspend inline fun CoroutineScope.withMinecraft(block: (mc: Minecraft) -> Unit) {
    while (isActive) {
        if (MinecraftAccessor.instance() != null)
            break
        delay(1L)
    }
    while (isActive) {
        block(MinecraftAccessor.instance())
    }
}

internal suspend fun inputProcessingCoroutineScope() = coroutineScope {
    val aiming = ControllerAimingHandler()
    launch(CoroutineName("Controller Input Processing")) {
        withMinecraft {
            ControllerSupport.manager.tick()
            tickMappings()
        }
    }
    launch(CoroutineName("Controller Aim Processing")) {
        withMinecraft {
            aiming.tick((it as MinecraftAccessor).timer().field_2370)
        }
    }
}

private fun tickMappings() {
    val controller = ControllerSupport.manager.currentController ?: return
    val minecraft = MinecraftAccessor.instance()
    if (minecraft.currentScreen == null) {
        val mc = minecraft as MinecraftAccessor
        if (mc.lastInteraction() > mc.currentTicks()) mc.setLastInteraction(mc.currentTicks())
    }
    ControllerSupport.isControllerActive = false
    for (action in MappingActions.all()) {
        if (action.contexts.none { a -> MappingActionContext.current == a })
            continue
        val ctx = ControllerSupport.mappingView[action.id]?.getContextFor(controller)
        if (ctx != null) {
            ControllerSupport.isControllerActive = true
            if (!action.isExecutorInternalUseOnly)
                action.executor.perform(ctx)
        }
    }
    if (Keyboard.getEventKeyState())
        ControllerSupport.isControllerActive = false
}

