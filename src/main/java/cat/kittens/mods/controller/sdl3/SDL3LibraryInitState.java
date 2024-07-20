package cat.kittens.mods.controller.sdl3;

import cat.kittens.mods.controller.exception.SDL3NativeLoadingFailException;

import java.util.Optional;

public class SDL3LibraryInitState {
    private static SDL3NativeLoadingFailException INIT_ERROR;

    public static Optional<SDL3NativeLoadingFailException> getInitializationError() {
        return Optional.ofNullable(INIT_ERROR);
    }

    public static void setInitializationError(SDL3NativeLoadingFailException err) {
        INIT_ERROR = err;
    }
}
