package cat.kittens.mods.controller.sdl3;

import cat.kittens.mods.controller.lib.ControllerLocation;
import cat.kittens.mods.controller.lib.IGamepadDeviceId;
import dev.isxander.sdl3java.api.gamepad.SDL_Gamepad;
import dev.isxander.sdl3java.api.joystick.SDL_JoystickID;

public class SDL3GamepadDeviceId implements IGamepadDeviceId {
    public final SDL_Gamepad handle;
    public final SDL_JoystickID joyId;
    private final String name;
    private final ControllerLocation location;

    public static String comparableId(SDL_JoystickID id) {
        return "SDL3.JoystickId+" + id;
    }

    public SDL3GamepadDeviceId(SDL_Gamepad handle, SDL_JoystickID joyId, String name, ControllerLocation location) {
        this.handle = handle;
        this.joyId = joyId;
        this.name = name;
        this.location = location;
    }

    @Override
    public ControllerLocation location() {
        return location;
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof SDL3GamepadDeviceId p)
            return p.handle.equals(handle);
        return false;
    }

    @Override
    public String name() {
        return name;
    }

    @Override
    public String toString() {
        return comparableId(joyId);
    }

    @Override
    public int hashCode() {
        return handle.hashCode();
    }
}
