package cat.kittens.mods.controller;

import cat.kittens.mods.controller.config.ControllerModConfig;
import cat.kittens.mods.controller.input.ControllerAimingHandler;
import cat.kittens.mods.controller.input.ControllerMovementHandler;
import cat.kittens.mods.controller.input.DefaultControllerMapping;
import cat.kittens.mods.controller.input.IControllerMapping;
import cat.kittens.mods.controller.lib.IGamepadDevice;
import cat.kittens.mods.controller.lib.IGamepadDeviceId;
import cat.kittens.mods.controller.lib.IGamepadManager;
import cat.kittens.mods.controller.sdl3.SDL3GamepadManager;
import net.fabricmc.api.ClientModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ControllerSupport implements ClientModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("Controller Support");
    private static ControllerSupport instance;

    private boolean isLastInputController;
    private ControllerAimingHandler aimingHandler;
    private ControllerMovementHandler movementHandler;

    public IControllerMapping mapping() {
        return DefaultControllerMapping.mapping();
    }

    public ControllerAimingHandler aiming() {
        return aimingHandler;
    }

    public ControllerMovementHandler movement() {
        return movementHandler;
    }

    public ControllerModConfig config() {
        return ControllerModConfig.DEFAULT;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public IGamepadManager<Exception, IGamepadDevice<IGamepadDeviceId>, IGamepadDeviceId> manager() {
        return (IGamepadManager) SDL3GamepadManager.manager();
    }

    public boolean isCurrentInputMethodController() {
        return manager().currentController().isPresent() && isLastInputController;
    }

    public void setCurrentInputMethod(boolean isController) {
        isLastInputController = manager().currentController().isPresent() && isController;
    }

    public static ControllerSupport support() {
        return instance;
    }

    @Override
    public void onInitializeClient() {
        instance = this;
        aimingHandler = new ControllerAimingHandler();
        movementHandler = new ControllerMovementHandler();
    }
}
