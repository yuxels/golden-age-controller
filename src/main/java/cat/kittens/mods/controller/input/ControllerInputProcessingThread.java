package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.ControllerSupport;
import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;

public class ControllerInputProcessingThread extends Thread {
    private final ControllerAimingHandler aiming;

    public ControllerInputProcessingThread() {
        this.aiming = new ControllerAimingHandler();
        setName("Controller Input Processing");
        setDaemon(true);
    }

    @Override
    public void run() {
        var instance = MinecraftAccessor.instance();
        while (instance != null && instance.running)
            tick(((MinecraftAccessor) instance).timer().field_2370);
    }

    public void tickMappings() {
        ControllerSupport.support().manager().currentController().ifPresent(controller -> {
            var minecraft = MinecraftAccessor.instance();
            if (minecraft.currentScreen == null) {
                var mc = (MinecraftAccessor) minecraft;
                if (mc.lastInteraction() > mc.currentTicks())
                    mc.setLastInteraction(mc.currentTicks());
            }
            for (var action : MappingActions.registered()) {
                boolean isContextAllowed = false;
                for (var ctx : action.contexts()) {
                    if (MappingActionContext.current() == ctx) {
                        isContextAllowed = true;
                        break;
                    }
                }
                if (!isContextAllowed)
                    continue;
                ControllerSupport.support().mapping().find(action.id()).flatMap(mapping -> mapping.getContextFor(controller)).ifPresent(ctx -> {
                    ControllerSupport.support().setCurrentInputMethod(true);
                    if (!action.isExecutorInternalUseOnly())
                        action.executor().perform(ctx);
                });
            }
        });
    }

    public void tick(float delta) {
        ControllerSupport.support().manager().tick();
        aiming.tick(delta);
    }
}
