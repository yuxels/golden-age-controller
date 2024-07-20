package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.lib.IGamepadDevice;
import cat.kittens.mods.controller.lib.IGamepadDeviceId;
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;

public class ControllerAimingHandler {
    private double aimX, aimY;

    public ControllerAimingHandler() {
        aimX = 0;
        aimY = 0;
    }

    public void tick() {
        if (MinecraftAccessor.instance().player == null)
            return;
        ControllerSupport.support().manager().currentController().ifPresent(controller -> {
            processInput(controller);
            externalize();
        });
    }

    public void processInput(IGamepadDevice<IGamepadDeviceId> gamepad) {
        if (MinecraftAccessor.instance().currentScreen != null)
            return;
        double x = gamepad.chord().getAxisValue(ActionIds.AIM_RIGHT).orElse(0) -
                gamepad.chord().getAxisValue(ActionIds.AIM_LEFT).orElse(0);
        double y = gamepad.chord().getAxisValue(ActionIds.AIM_UP).orElse(0) -
                gamepad.chord().getAxisValue(ActionIds.AIM_DOWN).orElse(0);
        double length = Math.abs(Math.sqrt((x * x) + (y * y))), angle = Math.atan2(y, x);
        aimX = (float) (Math.cos(angle) * length) * ControllerSupport.support().config().rightStickXSensitivity();
        aimY = (float) (Math.sin(angle) * length) * ControllerSupport.support().config().rightStickYSensitivity();
    }

    public void externalize() {
        if (MinecraftAccessor.instance().currentScreen != null || (aimX == 0 && aimY == 0))
            return;
        float delta = ((MinecraftAccessor) MinecraftAccessor.instance()).timer().field_2370;
        MinecraftAccessor.instance().player
                .method_1362((float) aimX / 0.15f * delta, (float) aimY / 0.15f * delta);
    }
}
