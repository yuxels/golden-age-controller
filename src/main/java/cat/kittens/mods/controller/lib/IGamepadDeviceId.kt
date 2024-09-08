package cat.kittens.mods.controller.lib

/**
 * An object that uniquely identifies an input device within an Input API.
 */
public interface IGamepadDeviceId {
    override fun hashCode(): Int

    override fun equals(other: Any?): Boolean

    override fun toString(): String

    public val location: ControllerLocation

    public val name: String
}
