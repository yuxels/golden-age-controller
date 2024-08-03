package cat.kittens.mods.controller.lib;

public record GenericGamepadDevice<I extends IGamepadDeviceId>(
        I id, Type gamepadType, Input input
) implements IGamepadDevice<I> {
}
