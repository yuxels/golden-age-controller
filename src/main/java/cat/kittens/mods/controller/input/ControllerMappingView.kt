package cat.kittens.mods.controller.input

import cat.kittens.mods.controller.ControllerSupport
import cat.kittens.mods.controller.lib.GamepadDevice
import cat.kittens.mods.controller.lib.IGamepadDeviceId

public interface ControllerMappingView {
    public val mappings: Set<ControllerMapping>

    public operator fun get(id: MappingAction.Id): ControllerMapping?

    public operator fun get(action: MappingAction): ControllerMapping? {
        return get(action.id)
    }

    public fun overrideMapping(id: MappingAction.Id, supplier: (prev: ControllerMapping?) -> ControllerMapping)
}

public val ControllerMapping.value: Double?
    get() {
        val controller: GamepadDevice<IGamepadDeviceId> = ControllerSupport.manager.currentController ?: return null
        return getContextFor(controller)?.value
    }
