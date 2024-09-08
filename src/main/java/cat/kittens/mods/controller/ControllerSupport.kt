package cat.kittens.mods.controller

import cat.kittens.mods.controller.config.ControllerModConfig
import cat.kittens.mods.controller.input.ControllerMappingView
import cat.kittens.mods.controller.input.ControllerMappingViewImpl
import cat.kittens.mods.controller.input.processing.ControllerMovementHandler
import cat.kittens.mods.controller.input.processing.inputProcessingCoroutineScope
import cat.kittens.mods.controller.lib.GenericGamepadManager
import cat.kittens.mods.controller.sdl3.SDL3GamepadManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import net.mine_diver.unsafeevents.listener.EventListener
import net.modificationstation.stationapi.api.event.mod.InitEvent
import net.modificationstation.stationapi.api.mod.entrypoint.Entrypoint
import org.apache.logging.log4j.Logger

public object ControllerSupport {
    @Entrypoint.Logger("Controller Support")
    public lateinit var LOGGER: Logger

    public var isControllerActive: Boolean = false
        get() = field && manager.currentController != null

    public val movementHandler: ControllerMovementHandler by lazy {
        ControllerMovementHandler()
    }

    public val mappingView: ControllerMappingView by lazy {
        ControllerMappingViewImpl()
    }

    public val manager: GenericGamepadManager by lazy {
        SDL3GamepadManager() as GenericGamepadManager
    }

    public val coroutineScope: CoroutineScope by lazy {
        CoroutineScope(SupervisorJob() + Dispatchers.Default)
    }

    public var config: ControllerModConfig = ControllerModConfig.DEFAULT

    @EventListener(phase = InitEvent.POST_INIT_PHASE)
    private fun init(ignored: InitEvent) {
        manager.tryLibInit()
        coroutineScope.launch {
            inputProcessingCoroutineScope().join()
        }
    }
}

public inline val logger: Logger
    get() = ControllerSupport.LOGGER
