package cat.kittens.mods.controller.lib

public data class GenericGamepadDevice<I : IGamepadDeviceId>(
    override val id: I,
    override val vendor: GamepadVendorType,
    override val input: GamepadDeviceInputView
) : GamepadDevice<I>
