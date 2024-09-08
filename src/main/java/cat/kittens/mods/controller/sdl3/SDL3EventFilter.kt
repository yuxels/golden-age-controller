package cat.kittens.mods.controller.sdl3;

import com.sun.jna.Pointer;
import dev.isxander.sdl3java.api.events.SDL_EventFilter;
import dev.isxander.sdl3java.api.events.events.SDL_Event;

import static dev.isxander.sdl3java.api.SDL_bool.SDL_FALSE;
import static dev.isxander.sdl3java.api.SDL_bool.SDL_TRUE;
import static dev.isxander.sdl3java.api.events.SDL_EventType.SDL_EVENT_JOYSTICK_ADDED;
import static dev.isxander.sdl3java.api.events.SDL_EventType.SDL_EVENT_JOYSTICK_REMOVED;

public class SDL3EventFilter implements SDL_EventFilter {
    @Override
    public int filterEvent(Pointer userData, SDL_Event event) {
        return switch (event.type) {
            case SDL_EVENT_JOYSTICK_ADDED, SDL_EVENT_JOYSTICK_REMOVED -> SDL_TRUE;
            default -> SDL_FALSE;
        };
    }
}
