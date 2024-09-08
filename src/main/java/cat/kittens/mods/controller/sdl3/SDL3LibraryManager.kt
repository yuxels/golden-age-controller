package cat.kittens.mods.controller.sdl3

import cat.kittens.mods.controller.ControllerSupport
import cat.kittens.mods.controller.exception.SDL3NativeLoadingFailException
import cat.kittens.mods.controller.logger
import com.google.common.io.ByteStreams
import com.sun.jna.Memory
import dev.isxander.sdl3java.api.error.SdlError
import dev.isxander.sdl3java.api.gamepad.SdlGamepad
import dev.isxander.sdl3java.api.iostream.SdlIOStream
import dev.isxander.sdl3java.jna.SdlNativeLibraryLoader
import dev.isxander.sdl3java.jna.size_t
import org.apache.commons.lang3.SystemUtils
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

public object SDL3LibraryManager {
    /**
     * Loads SDL3 built-in native libraries or the ones available on your system if not available.
     * Returns an error message or null if everything goes alright.
     */
    public fun loadLibrary(): Result<Unit> = runCatching {
        runCatching {
            loadLibraryFromSystem()
        }
        try {
            loadBuiltInLibrary()
        } catch (e: SDL3NativeLoadingFailException) {
            logger.error("Failed to initialise SDL3: ", e)
            throw e
        }
        try {
            ControllerSupport::class.java.classLoader.getResourceAsStream("gamecontrollerdb.txt").use { `in` ->
                if (`in` == null) throw IOException("Failed to get built-in file: gamecontrollerdb.txt.")
                val data = ByteStreams.toByteArray(`in`)
                Memory(data.size.toLong()).use { mem ->
                    mem.write(0, data, 0, data.size)
                    val stream = SdlIOStream.SDL_IOFromConstMem(mem, size_t(data.size.toLong()))
                    val count = SdlGamepad.SDL_AddGamepadMappingsFromIO(stream, true)
                    if (count < 0) throw IOException("Failed to load gamepad mappings: " + SdlError.SDL_GetError())
                    else if (count == 0)
                        logger.error("No available gamepad mappings were found for this operating system.")
                    logger.info("Successfully loaded gamepad mappings.")
                }
            }
        } catch (e: IOException) {
            logger.error("Failed to load SDL3 controller mappings: ", e)
            throw e
        }
    }

    /**
     * Loads the SDL3 native libraries from the system.
     */
    @Throws(SDL3NativeLoadingFailException::class)
    public fun loadLibraryFromSystem() {
        logger.warn("Loading SDL3 libraries from system...")
        try {
            SdlNativeLibraryLoader.loadLibSDL3FromFilePathNow("SDL3")
        } catch (e: UnsatisfiedLinkError) {
            logger.error("Loading SDL3 libraries from system failed!")
            throw SDL3NativeLoadingFailException("Native library isn't available on system.", e)
        }
        logger.warn("Loading SDL3 libraries from succeeded...")
    }

    /**
     * Loads the SDL3 native libraries built on this mod.
     */
    @Throws(SDL3NativeLoadingFailException::class)
    public fun loadBuiltInLibrary() {
        val ext = builtInOSLibExt
            ?: throw SDL3NativeLoadingFailException("Your system couldn't provide a SDL3 native library, nor a built-in one is available for your system.")
        val temp: Path
        try {
            temp = Files.createTempFile("sdl3-natives", ext)
            Files.deleteIfExists(temp)
        } catch (e: IOException) {
            throw SDL3NativeLoadingFailException("Failed to create temporary SDL3 natives file (1).", e)
        }
        val libraryName = "libsdl4j-natives-$ext"
        try {
            ControllerSupport::class.java.classLoader.getResourceAsStream(libraryName).use { `in` ->
                if (`in` == null) throw IOException("Failed to get built-in library: $libraryName")
                Files.copy(`in`, temp)
            }
        } catch (e: IOException) {
            throw SDL3NativeLoadingFailException("Failed to create temporary SDL3 natives file (2).", e)
        }
        try {
            SdlNativeLibraryLoader.loadLibSDL3FromFilePathNow(temp.toAbsolutePath().toString())
        } catch (e: UnsatisfiedLinkError) {
            throw SDL3NativeLoadingFailException("Couldn't load a built-in native library for your system.", e)
        }
    }

    private val builtInOSLibExt: String?
        /**
         * Returns the extension of the built-in native libraries if they're
         * available for the given operating system.
         *
         *
         * Supported: Linux x86_64, Windows x86_64, Windows x86 (32-bit), macOS
         * ARM64, macOS x86_64
         */
        get() {
            val arch = System.getProperty("os.arch")
            val is64Bit = arch.contains("64")
            val isARM = arch.contains("arm") || arch.contains("aarch")
            val isLinux = is64Bit && SystemUtils.IS_OS_LINUX
            val isWindows = !isARM && SystemUtils.IS_OS_WINDOWS
            val isDarwin =
                is64Bit && (SystemUtils.IS_OS_MAC || SystemUtils.IS_OS_MAC_OSX)
            return if (isLinux && !isARM)
                "linux64.so"
            else
                if (isWindows)
                    if (is64Bit) "windows64.dll" else "windows32.dll"
                else
                    if (isDarwin) if (isARM) "macos-aarch64.dylib" else "macos-x86_64.dylib" else null
        }
}
