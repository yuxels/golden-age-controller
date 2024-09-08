package cat.kittens.mods.controller.sdl3

import com.sun.jna.Pointer
import dev.isxander.sdl3java.api.SDL_bool
import dev.isxander.sdl3java.api.events.SDL_EventFilter
import dev.isxander.sdl3java.api.events.SDL_EventType
import dev.isxander.sdl3java.api.events.events.SDL_Event

public object SDL3EventFilter : SDL_EventFilter {
    override fun filterEvent(userData: Pointer?, event: SDL_Event?): Int {
        return when (event?.type) {
            SDL_EventType.SDL_EVENT_JOYSTICK_ADDED, SDL_EventType.SDL_EVENT_JOYSTICK_REMOVED -> SDL_bool.SDL_TRUE
            else -> SDL_bool.SDL_FALSE
        }
    }
}
