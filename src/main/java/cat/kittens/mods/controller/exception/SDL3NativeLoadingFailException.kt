package cat.kittens.mods.controller.exception

/**
 * An exception that gets thrown when the native libraries for SDL3 fail to be loaded, or
 * there's no built-in native libraries found for your operating system and your system
 * can't provide them either.
 */
public class SDL3NativeLoadingFailException : Exception {
    public constructor(message: String) : super(message)
    public constructor(message: String, cause: Throwable) : super(message, cause)
}