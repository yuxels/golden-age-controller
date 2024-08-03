package cat.kittens.mods.controller.input;

import java.util.concurrent.atomic.AtomicLong;

public interface MappingActionExecutor {
    static MappingActionExecutor internal() {
        return (ctx) -> {
            throw new RuntimeException("This mapping is meant to be used internally by Mixin injections and cannot be executed directly.");
        };
    }

    static MappingActionExecutor withCooldown(long amountInMilliseconds, MappingActionExecutor callback) {
        AtomicLong lastExecution = new AtomicLong();
        return (ctx) -> {
            if (System.currentTimeMillis() > lastExecution.get() + amountInMilliseconds) {
                callback.perform(ctx);
                lastExecution.set(System.currentTimeMillis());
            }
        };
    }

    void perform(MappingExecutionContext ctx);
}
