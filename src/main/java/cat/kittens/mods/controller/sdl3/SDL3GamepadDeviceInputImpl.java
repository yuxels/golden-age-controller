package cat.kittens.mods.controller.sdl3;

import cat.kittens.mods.controller.lib.GenericGamepadDeviceInputImpl;

import java.time.Duration;

import static dev.isxander.sdl3java.api.gamepad.SdlGamepad.SDL_RumbleGamepad;

public class SDL3GamepadDeviceInputImpl extends GenericGamepadDeviceInputImpl {
    private final SDL3GamepadDeviceId id;

    public SDL3GamepadDeviceInputImpl(SDL3GamepadDeviceId id) {
        super();
        this.id = id;
    }

    @Override
    public void rumble(short lowFreq, short highFreq, Duration duration) {
        SDL_RumbleGamepad(id.handle, lowFreq, highFreq, (int) duration.toMillis());
    }
}
