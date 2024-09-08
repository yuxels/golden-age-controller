package cat.kittens.mods.controller.sdl3;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.exception.SDL3NativeLoadingFailException;
import cat.kittens.mods.controller.lib.ControllerLocation;
import cat.kittens.mods.controller.lib.GenericGamepadDevice;
import cat.kittens.mods.controller.lib.IGamepadDevice;
import cat.kittens.mods.controller.lib.IGamepadManager;
import cat.kittens.mods.controller.util.MathHelper;
import com.sun.jna.Pointer;
import dev.isxander.sdl3java.api.events.events.SDL_Event;
import dev.isxander.sdl3java.api.gamepad.SDL_Gamepad;
import dev.isxander.sdl3java.api.joystick.SDL_JoystickID;

import java.util.*;

import static cat.kittens.mods.controller.lib.IGamepadDevice.Input.Axis.*;
import static cat.kittens.mods.controller.lib.IGamepadDevice.Input.Button.*;
import static dev.isxander.sdl3java.api.SDL_bool.SDL_FALSE;
import static dev.isxander.sdl3java.api.SDL_bool.SDL_TRUE;
import static dev.isxander.sdl3java.api.SdlInit.SDL_Init;
import static dev.isxander.sdl3java.api.SdlSubSystemConst.*;
import static dev.isxander.sdl3java.api.error.SdlError.SDL_GetError;
import static dev.isxander.sdl3java.api.events.SDL_EventType.SDL_EVENT_JOYSTICK_ADDED;
import static dev.isxander.sdl3java.api.events.SDL_EventType.SDL_EVENT_JOYSTICK_REMOVED;
import static dev.isxander.sdl3java.api.events.SdlEvents.*;
import static dev.isxander.sdl3java.api.events.SdlEventsConst.SDL_PRESSED;
import static dev.isxander.sdl3java.api.gamepad.SDL_GamepadAxis.*;
import static dev.isxander.sdl3java.api.gamepad.SDL_GamepadButton.*;
import static dev.isxander.sdl3java.api.gamepad.SDL_GamepadType.*;
import static dev.isxander.sdl3java.api.gamepad.SdlGamepad.*;
import static dev.isxander.sdl3java.api.joystick.SdlJoystick.SDL_GetJoysticks;
import static dev.isxander.sdl3java.api.joystick.SdlJoystick.SDL_UpdateJoysticks;

public class SDL3GamepadManager implements IGamepadManager<SDL3NativeLoadingFailException, GenericGamepadDevice<SDL3GamepadDeviceId>, SDL3GamepadDeviceId> {
    public boolean isInitialized;
    private Map<String, GenericGamepadDevice<SDL3GamepadDeviceId>> gamepads;
    private GenericGamepadDevice<SDL3GamepadDeviceId> currentGamepad;

    private SDL3EventFilter eventFilter;
    private SDL_Event event;

    private static SDL3GamepadManager INSTANCE;
    
    public SDL3GamepadManager() {
        this.isInitialized = false;
        this.gamepads = new HashMap<>();
        this.currentGamepad = null;
    }
    
    public static SDL3GamepadManager manager() {
        return INSTANCE == null ? INSTANCE = new SDL3GamepadManager() : INSTANCE;
    }

    @Override
    public String identifier() {
        return "SDL3 Controller Manager";
    }

    @Override
    public boolean isInitialized() {
        return isInitialized;
    }

    @Override
    public Optional<SDL3NativeLoadingFailException> tryLibInit() {
        if (SDL3LibraryManager.loadLibrary().isPresent())
            return Optional.empty();
        if (SDL_Init(SDL_INIT_EVENTS | SDL_INIT_JOYSTICK | SDL_INIT_GAMEPAD) != 0) {
            String message = SDL_GetError();
            ControllerSupport.LOGGER.error("Failed to initialise SDL3: {}", message);
            return Optional.of(new SDL3NativeLoadingFailException(message));
        }
        SDL_SetEventFilter(eventFilter = new SDL3EventFilter(), Pointer.NULL);
        ControllerSupport.LOGGER.info("Successfully loaded SDL3.");
        isInitialized = true;
        tick();
        return Optional.empty();
    }

    @Override
    public void discoverAll() {
        if (!isInitialized())
            return;
        SDL_JoystickID[] joystickIds = SDL_GetJoysticks();
        for (int num = 0; num < joystickIds.length; num++) {
            SDL_JoystickID joyId = joystickIds[num];
            if (SDL_IsGamepad(joyId) == SDL_FALSE)
                continue;
            SDL_Gamepad gamepad = SDL_OpenGamepad(joyId);
            GenericGamepadDevice<SDL3GamepadDeviceId> data = create("index " + num, joyId, gamepad);
            if (data != null) {
                gamepads.put(data.id().toString(), data);
                if (currentGamepad == null)
                    currentGamepad = data;
            }
        }
    }

    private GenericGamepadDevice<SDL3GamepadDeviceId> create(String name, SDL_JoystickID joyId, SDL_Gamepad gamepad) {
        if (gamepad == null) {
            String err = SDL_GetError();
            ControllerSupport.LOGGER.error("Failed to open gamepad '{}': {}", name, err);
            return null;
        }
        short vendor = SDL_GetGamepadVendor(gamepad), product = SDL_GetGamepadProduct(gamepad);
        String deviceName = SDL_GetGamepadName(gamepad);
        long index = gamepads.values().stream()
                .filter(g -> g.id().location().vendor() == vendor && g.id().location().product() == product)
                .count();
        ControllerLocation location = new ControllerLocation(identifier(), vendor, product, (int) index);
        SDL3GamepadDeviceId id = new SDL3GamepadDeviceId(gamepad, joyId, deviceName, location);
        IGamepadDevice.Type gamepadType = getGamepadType(gamepad);
        ControllerSupport.LOGGER.info("Found controller '{}'!", id.name());
        return new GenericGamepadDevice<>(id, gamepadType, new SDL3GamepadDeviceInputImpl(id));
    }

    private IGamepadDevice.Type getGamepadType(SDL_Gamepad id) {
        int baseType = SDL_GetGamepadType(id);
        return switch (baseType) {
            case SDL_GAMEPAD_TYPE_STANDARD -> IGamepadDevice.Type.STANDARD;
            case SDL_GAMEPAD_TYPE_XBOX360 -> IGamepadDevice.Type.XBOX_360;
            case SDL_GAMEPAD_TYPE_XBOXONE -> IGamepadDevice.Type.XBOX_ONE;
            case SDL_GAMEPAD_TYPE_PS3 -> IGamepadDevice.Type.PS3;
            case SDL_GAMEPAD_TYPE_PS4 -> IGamepadDevice.Type.PS4;
            case SDL_GAMEPAD_TYPE_PS5 -> IGamepadDevice.Type.PS5;
            case SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_PRO -> IGamepadDevice.Type.NS_PRO;
            case SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_JOYCON_LEFT -> IGamepadDevice.Type.NS_JOYCON_LEFT;
            case SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_JOYCON_RIGHT -> IGamepadDevice.Type.NS_JOYCON_RIGHT;
            case SDL_GAMEPAD_TYPE_NINTENDO_SWITCH_JOYCON_PAIR -> IGamepadDevice.Type.NS_JOYCON_PAIR;
            default -> IGamepadDevice.Type.UNKNOWN;
        };
    }

    @Override
    public List<GenericGamepadDevice<SDL3GamepadDeviceId>> findAllGamepads() {
        return gamepads.values().stream().toList();
    }

    @Override
    public boolean isGamepad(SDL3GamepadDeviceId id) {
        return isGamepad(id.toString());
    }

    @Override
    public boolean isGamepad(String id) {
        if (!isInitialized())
            return false;
        return gamepads.containsKey(id);
    }

    @Override
    public Optional<GenericGamepadDevice<SDL3GamepadDeviceId>> findGamepad(SDL3GamepadDeviceId id) {
        return findGamepad(id.toString());
    }

    @Override
    public Optional<GenericGamepadDevice<SDL3GamepadDeviceId>> findGamepad(String id) {
        if (!isInitialized())
            return Optional.empty();
        return Optional.ofNullable(gamepads.get(id));
    }

    @Override
    public Optional<GenericGamepadDevice<SDL3GamepadDeviceId>> currentController() {
        return Optional.ofNullable(currentGamepad);
    }

    @Override
    public void setCurrentController(GenericGamepadDevice<SDL3GamepadDeviceId> currentGamepad) {
        this.currentGamepad = currentGamepad;
    }

    private void updateInputState(GenericGamepadDevice<SDL3GamepadDeviceId> gamepad) {
        SDL_Gamepad h = gamepad.id().handle;
        IGamepadDevice.Input i = gamepad.input();
        stick(i, LEFT_STICK_DOWN, LEFT_STICK_UP, SDL_GetGamepadAxis(h, SDL_GAMEPAD_AXIS_LEFTY));
        stick(i, LEFT_STICK_RIGHT, LEFT_STICK_LEFT, SDL_GetGamepadAxis(h, SDL_GAMEPAD_AXIS_LEFTX));
        stick(i, RIGHT_STICK_DOWN, RIGHT_STICK_UP, SDL_GetGamepadAxis(h, SDL_GAMEPAD_AXIS_RIGHTY));
        stick(i, RIGHT_STICK_RIGHT, RIGHT_STICK_LEFT, SDL_GetGamepadAxis(h, SDL_GAMEPAD_AXIS_RIGHTX));
        i.setAxisValue(
                LEFT_TRIGGER, toFloat(SDL_GetGamepadAxis(h, SDL_GAMEPAD_AXIS_LEFT_TRIGGER))
        );
        i.setAxisValue(
                RIGHT_TRIGGER, toFloat(SDL_GetGamepadAxis(h, SDL_GAMEPAD_AXIS_RIGHT_TRIGGER))
        );
        i.setButtonState(SOUTH, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_SOUTH) == SDL_PRESSED);
        i.setButtonState(NORTH, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_NORTH) == SDL_PRESSED);
        i.setButtonState(EAST, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_EAST) == SDL_PRESSED);
        i.setButtonState(WEST, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_WEST) == SDL_PRESSED);
        i.setButtonState(BACK, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_BACK) == SDL_PRESSED);
        i.setButtonState(GUIDE, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_GUIDE) == SDL_PRESSED);
        i.setButtonState(START, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_START) == SDL_PRESSED);
        i.setButtonState(LEFT_STICK, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_LEFT_STICK) == SDL_PRESSED);
        i.setButtonState(RIGHT_STICK, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_RIGHT_STICK) == SDL_PRESSED);
        i.setButtonState(LEFT_SHOULDER, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_LEFT_SHOULDER) == SDL_PRESSED);
        i.setButtonState(RIGHT_SHOULDER, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_RIGHT_SHOULDER) == SDL_PRESSED);
        i.setButtonState(DPAD_UP, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_DPAD_UP) == SDL_PRESSED);
        i.setButtonState(DPAD_DOWN, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_DPAD_LEFT) == SDL_PRESSED);
        i.setButtonState(MISC1, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_MISC1) == SDL_PRESSED);
        i.setButtonState(MISC2, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_MISC2) == SDL_PRESSED);
        i.setButtonState(MISC3, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_MISC3) == SDL_PRESSED);
        i.setButtonState(MISC4, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_MISC4) == SDL_PRESSED);
        i.setButtonState(MISC5, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_MISC5) == SDL_PRESSED);
        i.setButtonState(MISC6, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_MISC6) == SDL_PRESSED);
        i.setButtonState(RIGHT_PADDLE1, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_RIGHT_PADDLE1) == SDL_PRESSED);
        i.setButtonState(RIGHT_PADDLE2, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_RIGHT_PADDLE2) == SDL_PRESSED);
        i.setButtonState(LEFT_PADDLE1, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_LEFT_PADDLE1) == SDL_PRESSED);
        i.setButtonState(LEFT_PADDLE2, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_LEFT_PADDLE2) == SDL_PRESSED);
        i.setButtonState(TOUCHPAD, SDL_GetGamepadButton(h, SDL_GAMEPAD_BUTTON_TOUCHPAD) == SDL_PRESSED);
    }

    private void stick(
            IGamepadDevice.Input input,
            IGamepadDevice.Input.Axis axisPos, IGamepadDevice.Input.Axis axisNeg,
            short sdl
    ) {
        float value = toFloat(sdl);
        input.setAxisValue(axisPos, value > 0 ? value : 0);
        input.setAxisValue(axisNeg, value < 0 ? -value : 0);
    }

    private float toFloat(short sdl) {
        return MathHelper.clampedLerpFromProgress(sdl, Short.MIN_VALUE, 0, -1f, 0f) +
                MathHelper.clampedLerpFromProgress(sdl, 0, Short.MAX_VALUE, 0f, 1f);
    }

    private void connect(SDL_JoystickID joyId) {
        if (joyId == null || SDL_IsGamepad(joyId) != SDL_TRUE)
            return;
        var gamepad = SDL_OpenGamepad(joyId);
        var controller = create("New device", joyId, gamepad);
        if (controller != null) {
            gamepads.put(controller.id().toString(), controller);
            if (currentGamepad == null)
                currentGamepad = controller;
        }
    }

    private void disconnect(SDL_JoystickID joyId) {
        if (joyId == null)
            return;
        var gamepad = gamepads.remove(SDL3GamepadDeviceId.comparableId(joyId));
        if (gamepad == null)
            return;
        ControllerSupport.LOGGER.info("Device '{}' has been disconnected.", gamepad.id().name());
        if (currentGamepad != null && currentGamepad.id().joyId.equals(joyId))
            setCurrentController(gamepads.values().stream().findFirst().orElse(null));
        SDL_CloseGamepad(gamepad.id().handle);
    }

    @Override
    public void tick() {
        gamepads.values().forEach(this::updateInputState);
        SDL_PumpEvents();
        if (event == null)
            event = new SDL_Event();
        while (SDL_PollEvent(event) == SDL_TRUE) {
            switch (event.type) {
                case SDL_EVENT_JOYSTICK_ADDED -> connect(event.jdevice.which);
                case SDL_EVENT_JOYSTICK_REMOVED -> disconnect(event.jdevice.which);
            }
        }
        SDL_UpdateGamepads();
        SDL_UpdateJoysticks();
    }
}
