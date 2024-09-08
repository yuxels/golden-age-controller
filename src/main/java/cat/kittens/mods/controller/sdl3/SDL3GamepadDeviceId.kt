package cat.kittens.mods.controller.sdl3

import cat.kittens.mods.controller.lib.ControllerLocation
import cat.kittens.mods.controller.lib.IGamepadDeviceId
import dev.isxander.sdl3java.api.gamepad.SDL_Gamepad
import dev.isxander.sdl3java.api.joystick.SDL_JoystickID

public data class SDL3GamepadDeviceId(
    public val handle: SDL_Gamepad,
    public val joyId: SDL_JoystickID,
    public override val name: String,
    public override val location: ControllerLocation
) : IGamepadDeviceId {
    override fun equals(other: Any?): Boolean {
        if (other is SDL3GamepadDeviceId) return other.handle == handle
        return false
    }

    override fun toString(): String {
        return comparableId(joyId)
    }

    override fun hashCode(): Int {
        return handle.hashCode()
    }

    public companion object {
        public fun comparableId(id: SDL_JoystickID): String {
            return "SDL3.JoystickId+$id"
        }
    }
}
