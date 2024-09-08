package cat.kittens.mods.controller.input

import cat.kittens.mods.controller.lib.GamepadAxisKind
import cat.kittens.mods.controller.lib.GamepadButtonKind
import cat.kittens.mods.controller.lib.GenericGamepadDeviceType
import cat.kittens.mods.controller.lib.get

public interface ControllerMapping {
    public val action: MappingAction

    public fun getContextFor(gamepad: GenericGamepadDeviceType): MappingExecutionContext?

    public fun withContextSupplier(
        supplier: (device: GenericGamepadDeviceType) -> MappingExecutionContext?
    ): ControllerMapping {
        val outer = this
        return object : ControllerMapping {
            override val action: MappingAction
                get() = outer.action

            override fun getContextFor(gamepad: GenericGamepadDeviceType): MappingExecutionContext? {
                return supplier(gamepad)
            }
        }
    }

    public companion object {
        @JvmName("create")
        public operator fun invoke(
            action: MappingAction,
            vararg buttons: GamepadButtonKind
        ): ControllerMapping {
            return invoke(action, buttons.toList(), emptyList())
        }

        @JvmName("create")
        public operator fun invoke(
            action: MappingAction,
            vararg axes: GamepadAxisKind
        ): ControllerMapping {
            return invoke(action, emptyList(), axes.toList())
        }

        public fun createContextSupplier(
            buttons: List<GamepadButtonKind>,
            axes: List<GamepadAxisKind>
        ): (GenericGamepadDeviceType) -> MappingExecutionContext? = block@{ gamepad ->
            if (buttons.isEmpty() && axes.isEmpty()) return@block null
            val buttonChordActive = buttons.all { gamepad.input[it].current ?: false }
            val axisChordActive = axes.all { (gamepad.input[it].current ?: Float.MIN_VALUE) > 0f }
            if (buttonChordActive && axisChordActive) {
                var held = !buttons.any { gamepad.input[it].previous != true }
                var value = 1.0
                for (axis in axes) {
                    val v = gamepad.input[axis]
                    if ((v.previous ?: 0f) <= 0f) held = false
                    val curr = gamepad.input[axis].current ?: 1f
                    if (curr > value) value = curr.toDouble()
                }
                return@block MappingExecutionContext(value, held)
            }
            null
        }

        @JvmName("create")
        public operator fun invoke(
            action: MappingAction,
            buttons: List<GamepadButtonKind>,
            axes: List<GamepadAxisKind>
        ): ControllerMapping {
            val supplier = createContextSupplier(buttons, axes)
            return object : ControllerMapping {
                override val action: MappingAction = action

                override fun getContextFor(gamepad: GenericGamepadDeviceType): MappingExecutionContext? =
                    supplier(gamepad)
            }
        }
    }
}
