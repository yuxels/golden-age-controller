package cat.kittens.mods.controller.sdl3

import cat.kittens.mods.controller.exception.SDL3NativeLoadingFailException
import cat.kittens.mods.controller.lib.*
import cat.kittens.mods.controller.logger
import cat.kittens.mods.controller.screen.uiId
import cat.kittens.mods.controller.util.MathHelper
import com.sun.jna.Pointer
import dev.isxander.sdl3java.api.SDL_bool
import dev.isxander.sdl3java.api.SdlInit
import dev.isxander.sdl3java.api.SdlSubSystemConst
import dev.isxander.sdl3java.api.error.SdlError
import dev.isxander.sdl3java.api.events.SDL_EventType
import dev.isxander.sdl3java.api.events.SdlEvents
import dev.isxander.sdl3java.api.events.SdlEventsConst
import dev.isxander.sdl3java.api.events.events.SDL_Event
import dev.isxander.sdl3java.api.gamepad.*
import dev.isxander.sdl3java.api.joystick.SDL_JoystickID
import dev.isxander.sdl3java.api.joystick.SdlJoystick

private val notInitializedError: Throwable
    get() = IllegalStateException("Operation can't be performed. Gamepad manager wasn't initialized.")

public class SDL3GamepadManager :
    GamepadManager<SDL3NativeLoadingFailException, GenericGamepadDevice<SDL3GamepadDeviceId>, SDL3GamepadDeviceId> {
    override var isInitialized: Boolean = false

    private val gamepads: MutableMap<String, GenericGamepadDevice<SDL3GamepadDeviceId>> = mutableMapOf()

    override var currentController: GenericGamepadDevice<SDL3GamepadDeviceId>? = null
        set(value) {
            logger.info("Setting current gamepad to " + (value?.uiId ?: "[None]"))
            field = value
        }
    private var event: SDL_Event? = null

    override val identifier: String = "SDL3 Controller Manager"

    override fun tryLibInit(): Result<Unit> {
        val libLoadRes = SDL3LibraryManager.loadLibrary()
        if (libLoadRes.isFailure)
            return Result.failure(libLoadRes.exceptionOrNull()!!)
        val flags =
            SdlSubSystemConst.SDL_INIT_EVENTS or SdlSubSystemConst.SDL_INIT_JOYSTICK or SdlSubSystemConst.SDL_INIT_GAMEPAD
        if (SdlInit.SDL_Init(flags) != 0) {
            val message = SdlError.SDL_GetError()
            logger.error("Failed to initialise SDL3: {}", message)
            return Result.failure(SDL3NativeLoadingFailException(message))
        }
        SdlEvents.SDL_SetEventFilter(SDL3EventFilter, Pointer.NULL)
        isInitialized = true
        logger.info("Successfully loaded SDL3.")
        tick()
        return Result.success(Unit)
    }

    override fun discoverAll(): Result<Unit> {
        if (!isInitialized)
            return Result.failure(notInitializedError)
        SdlJoystick.SDL_GetJoysticks()
            .filter { SdlGamepad.SDL_IsGamepad(it) == SDL_bool.SDL_TRUE }
            .forEachIndexed { index, id ->
                create("index $index", id, SdlGamepad.SDL_OpenGamepad(id))?.let { data ->
                    gamepads["${data.id}"] = data
                    currentController = currentController ?: data
                }
            }
        return Result.success(Unit)
    }

    private fun create(
        name: String,
        joyId: SDL_JoystickID,
        gamepad: SDL_Gamepad?
    ): GenericGamepadDevice<SDL3GamepadDeviceId>? {
        if (gamepad == null) {
            val err = SdlError.SDL_GetError()
            logger.error("Failed to open gamepad '{}': {}", name, err)
            return null
        }
        val vendor = SdlGamepad.SDL_GetGamepadVendor(gamepad)
        val product = SdlGamepad.SDL_GetGamepadProduct(gamepad)
        val deviceName = SdlGamepad.SDL_GetGamepadName(gamepad)
        val index = gamepads.values.count { it.id.location.vendor == vendor && it.id.location.product == product }
        val location = ControllerLocation(identifier, vendor, product, index)
        val id = SDL3GamepadDeviceId(gamepad, joyId, deviceName, location)
        val gamepadType = getGamepadType(gamepad)
        logger.info("Found controller '{}'!", id.name)
        return GenericGamepadDevice(id, gamepadType, SDL3GamepadDeviceInputImpl(id))
    }

    private fun getGamepadType(id: SDL_Gamepad): GamepadVendorType {
        val baseType = SdlGamepad.SDL_GetGamepadType(id)
        return when (baseType) {
            SDL_GamepadType.SDL_GAMEPAD_TYPE_STANDARD -> GamepadVendorType.Standard
            SDL_GamepadType.SDL_GAMEPAD_TYPE_XBOX360 -> GamepadVendorType.Xbox360
            SDL_GamepadType.SDL_GAMEPAD_TYPE_XBOXONE -> GamepadVendorType.XboxOne
            SDL_GamepadType.SDL_GAMEPAD_TYPE_PS3 -> GamepadVendorType.PS3
            SDL_GamepadType.SDL_GAMEPAD_TYPE_PS4 -> GamepadVendorType.PS4
            SDL_GamepadType.SDL_GAMEPAD_TYPE_PS5 -> GamepadVendorType.DualSense
            SDL_GamepadType.SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_PRO -> GamepadVendorType.NintendoSwitchProController
            SDL_GamepadType.SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_JOYCON_LEFT -> GamepadVendorType.NintendoSwitchJoyConLeft
            SDL_GamepadType.SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_JOYCON_RIGHT -> GamepadVendorType.NintendoSwitchJoyConRight
            SDL_GamepadType.SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_JOYCON_PAIR -> GamepadVendorType.NintendoSwitchJoyConPair
            else -> GamepadVendorType.Unknown
        }
    }

    override fun findAllGamepads(): Result<List<GenericGamepadDevice<SDL3GamepadDeviceId>>> {
        if (!isInitialized)
            return Result.failure(notInitializedError)
        return Result.success(gamepads.values.toList())
    }

    override fun hasGamepad(id: SDL3GamepadDeviceId): Result<Boolean> {
        if (!isInitialized)
            return Result.failure(notInitializedError)
        return hasGamepad(id.toString())
    }

    override fun hasGamepad(id: String): Result<Boolean> {
        if (!isInitialized)
            return Result.failure(notInitializedError)
        return Result.success(gamepads.containsKey(id))
    }

    override fun findGamepadOrNull(id: SDL3GamepadDeviceId): Result<GenericGamepadDevice<SDL3GamepadDeviceId>?> {
        if (!isInitialized)
            return Result.failure(notInitializedError)
        return findGamepadOrNull(id.toString())
    }

    override fun findGamepadOrNull(id: String): Result<GenericGamepadDevice<SDL3GamepadDeviceId>?> {
        if (!isInitialized)
            return Result.failure(notInitializedError)
        return Result.success(gamepads[id])
    }

    private fun updateInputState(gamepad: GenericGamepadDevice<SDL3GamepadDeviceId>) {
        val h = gamepad.id.handle
        val i = gamepad.input
        stick(
            i,
            GamepadAxisKind.LeftStickDown,
            GamepadAxisKind.LeftStickUp,
            SdlGamepad.SDL_GetGamepadAxis(h, SDL_GamepadAxis.SDL_GAMEPAD_AXIS_LEFTY)
        )
        stick(
            i,
            GamepadAxisKind.LeftStickRight,
            GamepadAxisKind.LeftStickLeft,
            SdlGamepad.SDL_GetGamepadAxis(h, SDL_GamepadAxis.SDL_GAMEPAD_AXIS_LEFTX)
        )
        stick(
            i,
            GamepadAxisKind.RightStickDown,
            GamepadAxisKind.RightStickUp,
            SdlGamepad.SDL_GetGamepadAxis(h, SDL_GamepadAxis.SDL_GAMEPAD_AXIS_RIGHTY)
        )
        stick(
            i,
            GamepadAxisKind.RightStickRight,
            GamepadAxisKind.RightStickLeft,
            SdlGamepad.SDL_GetGamepadAxis(h, SDL_GamepadAxis.SDL_GAMEPAD_AXIS_RIGHTX)
        )
        i[GamepadAxisKind.LeftTrigger] =
            toFloat(SdlGamepad.SDL_GetGamepadAxis(h, SDL_GamepadAxis.SDL_GAMEPAD_AXIS_LEFT_TRIGGER))
        i[GamepadAxisKind.RightTrigger] =
            toFloat(SdlGamepad.SDL_GetGamepadAxis(h, SDL_GamepadAxis.SDL_GAMEPAD_AXIS_RIGHT_TRIGGER))
        i[GamepadButtonKind.South] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_SOUTH) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.North] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_NORTH) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.East] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_EAST) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.West] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_WEST) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.Back] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_BACK) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.Guide] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_GUIDE) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.Start] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_START) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.LeftStickClick] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_LEFT_STICK
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.RightStickClick] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_RIGHT_STICK
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.LeftBumper] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_LEFT_SHOULDER
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.RightBumper] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_RIGHT_SHOULDER
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.DirectionalUp] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_DPAD_UP
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.DirectionalDown] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_DPAD_LEFT
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.Misc] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_MISC1) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.MiscSecondary] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_MISC2) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.MiscTertiary] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_MISC3) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.MiscQuaternary] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_MISC4) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.MiscQuinary] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_MISC5) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.MiscSenary] =
            SdlGamepad.SDL_GetGamepadButton(h, SDL_GamepadButton.SDL_GAMEPAD_BUTTON_MISC6) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.RightPaddle] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_RIGHT_PADDLE1
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.RightPaddleSecondary] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_RIGHT_PADDLE2
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.LeftPaddle] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_LEFT_PADDLE1
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.LeftPaddleSecondary] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_LEFT_PADDLE2
        ) == SdlEventsConst.SDL_PRESSED
        i[GamepadButtonKind.TouchpadClick] = SdlGamepad.SDL_GetGamepadButton(
            h,
            SDL_GamepadButton.SDL_GAMEPAD_BUTTON_TOUCHPAD
        ) == SdlEventsConst.SDL_PRESSED
    }

    private fun stick(
        input: GamepadDeviceInputView,
        axisPos: GamepadAxisKind, axisNeg: GamepadAxisKind,
        sdl: Short
    ) {
        val value = toFloat(sdl)
        input[axisPos] = value.coerceAtLeast(0f)
        input[axisNeg] = (-value).coerceAtLeast(0f)
    }

    private fun toFloat(sdl: Short): Float =
        MathHelper.clampedLerpFromProgress(sdl.toFloat(), Short.MIN_VALUE.toFloat(), 0f, -1f, 0f) +
                MathHelper.clampedLerpFromProgress(sdl.toFloat(), 0f, Short.MAX_VALUE.toFloat(), 0f, 1f)

    private fun connect(joyId: SDL_JoystickID?) {
        if (joyId == null || SdlGamepad.SDL_IsGamepad(joyId) != SDL_bool.SDL_TRUE) return
        val gamepad = SdlGamepad.SDL_OpenGamepad(joyId)
        val controller = create("New device", joyId, gamepad)
        if (controller != null) {
            gamepads[controller.id.toString()] = controller
            currentController = currentController ?: controller
        }
    }

    private fun disconnect(joyId: SDL_JoystickID?) {
        if (joyId == null) return
        val gamepad = gamepads.remove(SDL3GamepadDeviceId.comparableId(joyId)) ?: return
        logger.info("Device '{}' has been disconnected.", gamepad.id.name)
        if (currentController?.id?.joyId == joyId)
            currentController = gamepads.values.firstOrNull()
        SdlGamepad.SDL_CloseGamepad(gamepad.id.handle)
    }

    override fun tick() {
        gamepads.values.forEach(this::updateInputState)
        SdlEvents.SDL_PumpEvents()
        if (event == null) event = SDL_Event()
        while (SdlEvents.SDL_PollEvent(event) == SDL_bool.SDL_TRUE) {
            when (event!!.type) {
                SDL_EventType.SDL_EVENT_JOYSTICK_ADDED -> connect(event!!.jdevice.which)
                SDL_EventType.SDL_EVENT_JOYSTICK_REMOVED -> disconnect(event!!.jdevice.which)
            }
        }
        SdlGamepad.SDL_UpdateGamepads()
        SdlJoystick.SDL_UpdateJoysticks()
    }
}
