package cat.kittens.mods.controller.input;

import cat.kittens.mods.controller.mixin.accessor.MinecraftAccessor;
import net.minecraft.client.gui.screen.ChatScreen;

public class ActionExecutors {
    public static IReversibleActionExecutor withinGameplay(IReversibleActionExecutor exec) {
        return (ctx) -> {
            if (ctx.minecraft().currentScreen == null) exec.perform(ctx);
        };
    }

    public static final IReversibleActionExecutor EMPTY = IReversibleActionExecutor.EMPTY;
    public static final IReversibleActionExecutor BREAK = withinGameplay(ctx -> {
        MinecraftAccessor mc = (MinecraftAccessor) ctx.minecraft();
        if (!ctx.release() && (mc.currentTicks() - mc.lastInteraction()) >= mc.timer().field_2368 / 4.0F) {
            mc.interact(0);
            mc.setLastInteraction(mc.currentTicks());
        }
    });
    public static final IReversibleActionExecutor INTERACT = withinGameplay(ctx -> {
        MinecraftAccessor mc = (MinecraftAccessor) ctx.minecraft();
        if (!ctx.release() && (mc.currentTicks() - mc.lastInteraction()) >= mc.timer().field_2368 / 4.0F) {
            mc.interact(1);
            mc.setLastInteraction(mc.currentTicks());
        }
    });
    public static final IReversibleActionExecutor PICK_BLOCK = withinGameplay(ctx -> {
        MinecraftAccessor mc = (MinecraftAccessor) ctx.minecraft();
        if (!ctx.release()) {
            mc.pickBlock();
        }
    });
    public static final IReversibleActionExecutor JUMP = EMPTY;
    public static final IReversibleActionExecutor OPEN_INVENTORY = withinGameplay(ctx -> {
        ctx.minecraft().player.method_136(ctx.minecraft().options.inventoryKey.code, !ctx.release());
    });
    public static final IReversibleActionExecutor WALK_FORWARD = EMPTY;
    public static final IReversibleActionExecutor WALK_BACKWARD = EMPTY;
    public static final IReversibleActionExecutor WALK_LEFTWARD = EMPTY;
    public static final IReversibleActionExecutor WALK_RIGHTWARD = EMPTY;
    public static final IReversibleActionExecutor DROP_ITEM = withinGameplay(ctx -> {
        if (!ctx.release())
            ctx.minecraft().player.dropSelectedItem();
    });
    public static final IReversibleActionExecutor SNEAK = EMPTY;
    public static final IReversibleActionExecutor OPEN_CHAT = withinGameplay(ctx -> {
        if (!ctx.release() && ctx.minecraft().isWorldRemote())
            ctx.minecraft().setScreen(new ChatScreen());
    });
    public static final IReversibleActionExecutor AIM_LEFT = EMPTY;
    public static final IReversibleActionExecutor AIM_RIGHT = EMPTY;
    public static final IReversibleActionExecutor AIM_UP = EMPTY;
    public static final IReversibleActionExecutor AIM_DOWN = EMPTY;
}
