package cat.kittens.mods.controller.input;

import com.google.common.collect.ImmutableSet;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.InventoryScreen;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class MappingActions {
    private static Map<MappingAction.Id, MappingAction> MAPPINGS;
    public static final MappingAction BREAK =
            register(MappingAction.inGame("minecraft:break", true, MappingActionExecutor.internal()));
    public static final MappingAction INTERACT =
            register(MappingAction.inGame("minecraft:interact", true, MappingActionExecutor.internal()));
    public static final MappingAction PICK_BLOCK =
            register(MappingAction.inGame("minecraft:pick_block", ctx -> ctx.mc().pickBlock()));
    public static final MappingAction JUMP =
            register(MappingAction.inGame("minecraft:jump", true, MappingActionExecutor.internal()));
    public static final MappingAction OPEN_INVENTORY = register(MappingAction.inGame("minecraft:open_inventory",
            ctx -> ctx.minecraft().setScreen(new InventoryScreen(ctx.minecraft().player))));
    public static final MappingAction BACK_TO_GAME = register(MappingAction.create(
            "minecraft:back_to_game",
            MappingActionContext.listOf(
                    MappingActionContext.CONTAINER,
                    MappingActionContext.INVENTORY,
                    MappingActionContext.CHAT,
                    MappingActionContext.PAUSE
            ),
            ctx -> ctx.minecraft().setScreen(null)
    ));
    public static final MappingAction WALK_FORWARD =
            register(MappingAction.inGame("minecraft:walk_forward", true, MappingActionExecutor.internal()));
    public static final MappingAction WALK_BACKWARD =
            register(MappingAction.inGame("minecraft:walk_backward", true, MappingActionExecutor.internal()));
    public static final MappingAction WALK_LEFTWARD =
            register(MappingAction.inGame("minecraft:walk_leftward", true, MappingActionExecutor.internal()));
    public static final MappingAction WALK_RIGHTWARD =
            register(MappingAction.inGame("minecraft:walk_rightward", true, MappingActionExecutor.internal()));
    public static final MappingAction DROP_ITEM = register(MappingAction.inGame("minecraft:drop_item", ctx -> {
        if (!ctx.held()) ctx.minecraft().player.dropSelectedItem();
    }));
    public static final MappingAction SNEAK =
            register(MappingAction.inGame("minecraft:sneak", true, MappingActionExecutor.internal()));
    public static final MappingAction OPEN_CHAT = register(MappingAction.inGame("minecraft:open_chat", ctx -> {
        if (ctx.minecraft().isWorldRemote())
            ctx.minecraft().setScreen(new ChatScreen());
    }));
    public static final MappingAction AIM_LEFT =
            register(MappingAction.inGame("minecraft:aim_left", true, MappingActionExecutor.internal()));
    public static final MappingAction AIM_RIGHT =
            register(MappingAction.inGame("minecraft:aim_right", true, MappingActionExecutor.internal()));
    public static final MappingAction AIM_UP =
            register(MappingAction.inGame("minecraft:aim_up", true, MappingActionExecutor.internal()));
    public static final MappingAction AIM_DOWN =
            register(MappingAction.inGame("minecraft:aim_down", true, MappingActionExecutor.internal()));

    public static void clear() {
        MAPPINGS = new HashMap<>();
    }

    public static MappingAction register(MappingAction action) {
        if (MAPPINGS == null)
            clear();
        MAPPINGS.put(action.id(), action);
        return action;
    }

    public static Optional<MappingAction> getById(MappingAction.Id id) {
        return Optional.ofNullable(MAPPINGS.get(id));
    }

    public static ImmutableSet<MappingAction> registered() {
        return ImmutableSet.copyOf(MAPPINGS.values());
    }
}
