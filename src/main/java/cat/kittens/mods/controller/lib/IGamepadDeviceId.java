package cat.kittens.mods.controller.lib;

/**
 * An object that uniquely identifies an input device within an Input API.
 */
public interface IGamepadDeviceId {
    @Override
    int hashCode();

    @Override
    boolean equals(Object other);

    @Override
    public String toString();

    ControllerLocation location();

    String name();
}
