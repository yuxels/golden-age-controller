package cat.kittens.mods.controller.lib

import java.time.Duration

/**
 * Gamepad vendor/type.
 */
public enum class GamepadVendorType {
    Standard,
    Xbox360,
    XboxOne,
    PS3,
    PS4,
    DualSense,
    NintendoSwitchProController,
    NintendoSwitchJoyConLeft,
    NintendoSwitchJoyConRight,
    NintendoSwitchJoyConPair,
    Unknown
}

public enum class GamepadButtonKind {
    South,  /* Bottom face button (e.g. Xbox A button) */
    East,  /* Right face button (e.g. Xbox B button) */
    West,  /* Left face button (e.g. Xbox X button) */
    North,  /* Top face button (e.g. Xbox Y button) */
    Back,
    Guide,
    Start,
    LeftStickClick,
    RightStickClick,
    LeftBumper,
    RightBumper,
    DirectionalUp,
    DirectionalDown,
    DirectionalLeft,
    DirectionalRight,
    Misc,  /* Additional button (e.g. Xbox Series X share button, PS5 microphone button, Nintendo Switch Pro capture button, Amazon Luna microphone button, Google Stadia capture button) */
    RightPaddle,  /* Upper or primary paddle, under your right hand (e.g. Xbox Elite paddle P1) */
    LeftPaddle,  /* Upper or primary paddle, under your left hand (e.g. Xbox Elite paddle P3) */
    RightPaddleSecondary,  /* Lower or secondary paddle, under your right hand (e.g. Xbox Elite paddle P2) */
    LeftPaddleSecondary,  /* Lower or secondary paddle, under your left hand (e.g. Xbox Elite paddle P4) */
    TouchpadClick,  /* PS4/PS5 touchpad button */
    MiscSecondary,  /* Additional button */
    MiscTertiary,  /* Additional button */
    MiscQuaternary,  /* Additional button */
    MiscQuinary,  /* Additional button */
    MiscSenary,  /* Additional button */
}

public enum class GamepadAxisKind {
    LeftStickUp,
    LeftStickLeft,
    LeftStickDown,
    LeftStickRight,
    RightStickUp,
    RightStickLeft,
    RightStickDown,
    RightStickRight,
    LeftTrigger,
    RightTrigger,
}

public typealias GenericGamepadDeviceType = GamepadDevice<*>

/**
 * Controller input device.
 */
public interface GamepadDevice<I : IGamepadDeviceId> {
    public val id: I

    public val input: GamepadDeviceInputView

    public val vendor: GamepadVendorType
}


public interface GamepadDeviceInputView {
    public fun buttonState(button: GamepadButtonKind): DoubleStateOutput<Boolean>

    public fun buttonState(button: GamepadButtonKind, active: Boolean)

    public fun deadZone(axis: GamepadAxisKind): Float

    public fun deadZone(axis: GamepadAxisKind, value: Float)

    public fun axisState(axis: GamepadAxisKind): DoubleStateOutput<Float>

    public fun axisState(axis: GamepadAxisKind, value: Float)

    public fun rumble(lowFreq: Short, highFreq: Short, duration: Duration)
}

public operator fun GamepadDeviceInputView.get(button: GamepadButtonKind): DoubleStateOutput<Boolean> =
    buttonState(button)

public operator fun GamepadDeviceInputView.set(button: GamepadButtonKind, active: Boolean): Unit =
    buttonState(button, active)

public operator fun GamepadDeviceInputView.get(axis: GamepadAxisKind): DoubleStateOutput<Float> =
    axisState(axis)

public operator fun GamepadDeviceInputView.set(axis: GamepadAxisKind, value: Float): Unit =
    axisState(axis, value)
