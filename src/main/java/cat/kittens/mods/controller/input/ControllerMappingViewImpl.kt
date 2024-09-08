package cat.kittens.mods.controller.input

import cat.kittens.mods.controller.lib.GamepadAxisKind
import cat.kittens.mods.controller.lib.GamepadButtonKind

public class ControllerMappingViewImpl(mappings: Set<ControllerMapping> = defaultSet()) : ControllerMappingView {
    private val _mappings = mappings.associateBy { it.action.id }.toMutableMap()

    override val mappings: Set<ControllerMapping>
        get() = _mappings.values.toSet()

    override fun overrideMapping(id: MappingAction.Id, supplier: (prev: ControllerMapping?) -> ControllerMapping) {
        _mappings.compute(id) { _, v -> supplier(v) }
    }

    override fun get(id: MappingAction.Id): ControllerMapping? {
        return _mappings[id]
    }

    private companion object {
        private fun defaultSet(): Set<ControllerMapping> = setOf(
            ControllerMapping(MappingActions.BREAK, GamepadAxisKind.RightTrigger),
            ControllerMapping(MappingActions.INTERACT, GamepadAxisKind.LeftTrigger),
            ControllerMapping(MappingActions.PICK_BLOCK, GamepadButtonKind.DirectionalLeft),
            ControllerMapping(MappingActions.JUMP, GamepadButtonKind.South),
            ControllerMapping(MappingActions.OPEN_INVENTORY, GamepadButtonKind.North),
            ControllerMapping(MappingActions.DROP_ITEM, GamepadButtonKind.East),
            ControllerMapping(MappingActions.SNEAK, GamepadButtonKind.West),
            ControllerMapping(MappingActions.OPEN_CHAT, GamepadButtonKind.DirectionalRight),
            ControllerMapping(
                MappingActions.WALK_FORWARD,
                GamepadAxisKind.LeftStickUp
            ),
            ControllerMapping(
                MappingActions.WALK_BACKWARD,
                GamepadAxisKind.LeftStickDown
            ),
            ControllerMapping(
                MappingActions.WALK_LEFTWARD,
                GamepadAxisKind.LeftStickLeft
            ),
            ControllerMapping(
                MappingActions.WALK_RIGHTWARD,
                GamepadAxisKind.LeftStickRight
            ),
            ControllerMapping(MappingActions.AIM_UP, GamepadAxisKind.RightStickUp),
            ControllerMapping(MappingActions.AIM_DOWN, GamepadAxisKind.RightStickDown),
            ControllerMapping(MappingActions.AIM_LEFT, GamepadAxisKind.RightStickLeft),
            ControllerMapping(
                MappingActions.AIM_RIGHT,
                GamepadAxisKind.RightStickRight
            ),
            ControllerMapping(MappingActions.BACK_TO_GAME, GamepadButtonKind.East)
        )
    }
}
