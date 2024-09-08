package cat.kittens.mods.controller.lib

import java.time.Duration
import java.util.*

public open class GenericGamepadDeviceInputImpl : GamepadDeviceInputView {
    protected var faces: MutableMap<GamepadButtonKind, DoubleStateOutput<Boolean>> =
        EnumMap(GamepadButtonKind::class.java)
    protected var axes: MutableMap<GamepadAxisKind, DoubleStateOutput<Float>> =
        EnumMap(GamepadAxisKind::class.java)
    protected var deadZones: MutableMap<GamepadAxisKind, Float> =
        EnumMap(GamepadAxisKind::class.java)

    override fun buttonState(button: GamepadButtonKind): DoubleStateOutput<Boolean> {
        return faces.computeIfAbsent(button) { DoubleStateOutput() }
    }

    override fun buttonState(button: GamepadButtonKind, active: Boolean) {
        get(button).set(active)
    }

    override fun deadZone(axis: GamepadAxisKind): Float {
        return deadZones.getOrDefault(axis, 0.25f)
    }

    override fun deadZone(axis: GamepadAxisKind, value: Float) {
        deadZones[axis] = value
    }

    override fun axisState(axis: GamepadAxisKind): DoubleStateOutput<Float> {
        return axes.computeIfAbsent(axis) { DoubleStateOutput() }
    }

    override fun axisState(axis: GamepadAxisKind, value: Float) {
        get(axis).set(if (value > deadZone(axis)) value else 0f)
    }

    override fun rumble(lowFreq: Short, highFreq: Short, duration: Duration) {
    }
}
