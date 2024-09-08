package cat.kittens.mods.controller.exception;

/**
 * An exception that gets thrown when the native libraries for SDL3 fail to be loaded, or
 * there's no built-in native libraries found for your operating system and your system
 * can't provide them either.
 */
public class SDL3NativeLoadingFailException extends Exception {
    public SDL3NativeLoadingFailException(String message) {
        super(message);
    }
    public SDL3NativeLoadingFailException(String message, Throwable cause) {
        super(message, cause);
    }
}