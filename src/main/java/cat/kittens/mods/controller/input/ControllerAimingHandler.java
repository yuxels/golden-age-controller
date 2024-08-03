package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;

public class ControllerAimingHandler {
    private double aimX, aimY;

    public ControllerAimingHandler() {
        aimX = 0;
        aimY = 0;
    }

    public void tick(float delta) {
        if (MinecraftAccessor.instance().currentScreen != null || MinecraftAccessor.instance().player == null)
            return;
        var controller = ControllerSupport.support().manager().currentController().orElse(null);
        if (controller == null)
            return;
        processInput();
        externalize(delta);
    }

    public void processInput() {
        if (MinecraftAccessor.instance().currentScreen != null || MinecraftAccessor.instance().player == null)
            return;
        double x = ControllerSupport.support().mapping().getCurrentValue(MappingActions.AIM_RIGHT).orElse(0) -
                ControllerSupport.support().mapping().getCurrentValue(MappingActions.AIM_LEFT).orElse(0);
        double y = ControllerSupport.support().mapping().getCurrentValue(MappingActions.AIM_UP).orElse(0) -
                ControllerSupport.support().mapping().getCurrentValue(MappingActions.AIM_DOWN).orElse(0);
        double length = Math.abs(Math.sqrt((x * x) + (y * y))), angle = Math.atan2(y, x);
        aimX = (Math.cos(angle) * length) * ControllerSupport.support().config().rightStickXSensitivity();
        aimY = (Math.sin(angle) * length) * ControllerSupport.support().config().rightStickYSensitivity();
    }

    public void externalize(float delta) {
        if (MinecraftAccessor.instance().currentScreen != null || MinecraftAccessor.instance().player == null
                || (aimX == 0 && aimY == 0))
            return;
        MinecraftAccessor.instance().player
                .method_1362((float) (aimX / 0.15f) * delta, (float) (aimY / 0.15f) * delta);
        aimX = 0.;
        aimY = 0.;
    }
}
