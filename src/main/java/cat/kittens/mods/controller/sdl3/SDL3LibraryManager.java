package cat.kittens.mods.controller.sdl3;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.exception.SDL3NativeLoadingFailException;
import com.google.common.io.ByteStreams;
import com.sun.jna.Memory;
import dev.isxander.sdl3java.jna.SdlNativeLibraryLoader;
import dev.isxander.sdl3java.jna.size_t;
import org.apache.commons.lang3.SystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static dev.isxander.sdl3java.api.error.SdlError.SDL_GetError;
import static dev.isxander.sdl3java.api.gamepad.SdlGamepad.SDL_AddGamepadMappingsFromIO;
import static dev.isxander.sdl3java.api.iostream.SdlIOStream.SDL_IOFromConstMem;

public class SDL3LibraryManager {
    /**
     * Loads SDL3 built-in native libraries or the ones available on your system if not available.
     * Returns an error message or null if everything goes alright.
     */
    public static Optional<SDL3NativeLoadingFailException> loadLibrary() {
        try {
            loadLibraryFromSystem();
        } catch (SDL3NativeLoadingFailException ignored) {
        }
        try {
            loadBuiltInLibrary();
        } catch (SDL3NativeLoadingFailException e) {
            ControllerSupport.LOGGER.error("Failed to initialise SDL3: ", e);
            return Optional.of(e);
        }
        try (InputStream in = ControllerSupport.class.getClassLoader().getResourceAsStream("gamecontrollerdb.txt")) {
            if (in == null)
                throw new IOException("Failed to get built-in file: gamecontrollerdb.txt.");
            byte[] data = ByteStreams.toByteArray(in);
            try (Memory mem = new Memory(data.length)) {
                mem.write(0, data, 0, data.length);
                var stream = SDL_IOFromConstMem(mem, new size_t(data.length));
                int count = SDL_AddGamepadMappingsFromIO(stream, true);
                if (count < 0)
                    throw new IOException("Failed to load gamepad mappings: " + SDL_GetError());
                else if (count == 0)
                    ControllerSupport.LOGGER.error("No available gamepad mappings were found for this operating system.");
                ControllerSupport.LOGGER.info("Successfully loaded gamepad mappings.");
            }
        } catch (IOException e) {
            ControllerSupport.LOGGER.error("Failed to load SDL3 controller mappings: ", e);
            return Optional.of(new SDL3NativeLoadingFailException("Failed to load SDL3 controller mappings.", e));
        }
        return Optional.empty();
    }

    /**
     * Loads the SDL3 native libraries from the system.
     */
    public static void loadLibraryFromSystem() throws SDL3NativeLoadingFailException {
        ControllerSupport.LOGGER.warn("Controller support mod: Loading SDL3 libraries from system...");
        try {
            SdlNativeLibraryLoader.loadLibSDL3FromFilePathNow("SDL3");
        } catch (UnsatisfiedLinkError e) {
            ControllerSupport.LOGGER.error("Controller support mod: Loading SDL3 libraries from system failed!");
            throw new SDL3NativeLoadingFailException("Native library isn't available on system.", e);
        }
        ControllerSupport.LOGGER.warn("Controller support mod: Loading SDL3 libraries from succeeded...");
    }

    /**
     * Loads the SDL3 native libraries built on this mod.
     */
    public static void loadBuiltInLibrary() throws SDL3NativeLoadingFailException {
        String ext = getBuiltInOSLibExt();
        if (ext == null)
            throw new SDL3NativeLoadingFailException("Your system couldn't provide a SDL3 native library, nor a built-in one is available for your system.");
        Path temp;
        try {
            temp = Files.createTempFile("sdl3-natives", ext);
            Files.deleteIfExists(temp);
        } catch (IOException e) {
            throw new SDL3NativeLoadingFailException("Failed to create temporary SDL3 natives file (1).", e);
        }
        String libraryName = "libsdl4j-natives-" + ext;
        try (InputStream in = ControllerSupport.class.getClassLoader().getResourceAsStream("sdl3-natives/" + libraryName)) {
            if (in == null)
                throw new IOException("Failed to get built-in library: " + libraryName);
            Files.copy(in, temp);
        } catch (IOException e) {
            throw new SDL3NativeLoadingFailException("Failed to create temporary SDL3 natives file (2).", e);
        }
        try {
            SdlNativeLibraryLoader.loadLibSDL3FromFilePathNow(temp.toAbsolutePath().toString());
        } catch (UnsatisfiedLinkError e) {
            throw new SDL3NativeLoadingFailException("Couldn't load a built-in native library for your system.", e);
        }
    }

    /**
     * Returns the extension of the built-in native libraries if they're
     * available for the given operating system.
     * <p></p>
     * Supported: Linux x86_64, Windows x86_64, Windows x86 (32-bit), macOS
     * ARM64, macOS x86_64
     */
    private static String getBuiltInOSLibExt() {
        String arch = System.getProperty("os.arch");
        boolean is64Bit = arch.contains("64");
        boolean isARM = arch.contains("arm") || arch.contains("aarch");
        boolean isLinux = is64Bit && SystemUtils.IS_OS_LINUX;
        boolean isWindows = !isARM && SystemUtils.IS_OS_WINDOWS;
        boolean isDarwin = is64Bit && (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX);
        return isLinux && !isARM ? "linux64.so"
                : isWindows ? is64Bit ? "windows64.dll" : "windows32.dll"
                : isDarwin ? isARM ? "macos-aarch64.dylib" : "macos-x86_64.dylib" : null;
    }
}
