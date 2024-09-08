package cat.kittens.mods.controller.sdl3

import cat.kittens.mods.controller.lib.GenericGamepadDeviceInputImpl
import dev.isxander.sdl3java.api.gamepad.SdlGamepad
import java.time.Duration

public class SDL3GamepadDeviceInputImpl(private val id: SDL3GamepadDeviceId) : GenericGamepadDeviceInputImpl() {
    override fun rumble(lowFreq: Short, highFreq: Short, duration: Duration) {
        SdlGamepad.SDL_RumbleGamepad(id.handle, lowFreq, highFreq, duration.toMillis().toInt())
    }
}
