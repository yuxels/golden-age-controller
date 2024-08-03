package cat.kittens.mods.controller.input;

import com.google.common.collect.ImmutableList;

/**
 * Provides action execution and information about a mapping.
 */
public interface MappingAction {
    static MappingAction inGame(Id id, MappingActionExecutor executor) {
        return create(id, MappingActionContext.listOf(MappingActionContext.IN_GAME), false, executor);
    }

    static MappingAction inGame(String id, MappingActionExecutor executor) {
        return inGame(Id.fromString(id), executor);
    }

    static MappingAction inGame(String id, boolean internal, MappingActionExecutor executor) {
        return create(Id.fromString(id), MappingActionContext.listOf(MappingActionContext.IN_GAME), internal, executor);
    }

    static MappingAction inGame(Id id, boolean internal, MappingActionExecutor executor) {
        return create(id, MappingActionContext.listOf(MappingActionContext.IN_GAME), internal, executor);
    }

    static MappingAction create(
            Id id, ImmutableList<MappingActionContext> contexts, MappingActionExecutor executor
    ) {
        return create(id, contexts, false, executor);
    }

    static MappingAction create(
            String id, ImmutableList<MappingActionContext> contexts, MappingActionExecutor executor
    ) {
        return create(Id.fromString(id), contexts, false, executor);
    }

    static MappingAction create(
            String id, ImmutableList<MappingActionContext> contexts, boolean internal, MappingActionExecutor executor
    ) {
        return create(Id.fromString(id), contexts, internal, executor);
    }

    static MappingAction create(
            Id id, ImmutableList<MappingActionContext> contexts,
            boolean internal, MappingActionExecutor executor
    ) {
        return new MappingAction() {
            @Override
            public Id id() {
                return id;
            }

            @Override
            public ImmutableList<MappingActionContext> contexts() {
                return contexts;
            }

            @Override
            public MappingActionExecutor executor() {
                return executor;
            }

            @Override
            public boolean isExecutorInternalUseOnly() {
                return internal;
            }
        };
    }

    /**
     * Unique identification for this action.
     */
    Id id();

    /**
     * Contexts where this mapping action could be executed.
     */
    ImmutableList<MappingActionContext> contexts();

    /**
     * Whether this mapping has an executor that is made for internal-use only.
     */
    boolean isExecutorInternalUseOnly();

    MappingActionExecutor executor();

    interface Id {
        static Id fromString(String value) {
            return new Id() {
                @Override
                public String toString() {
                    return value;
                }

                @Override
                public boolean equals(Object obj) {
                    if (obj instanceof Id id) {
                        return id.toString().equals(toString());
                    }
                    return false;
                }

                @Override
                public int hashCode() {
                    return value.hashCode();
                }
            };
        }

        @Override
        String toString();

        @Override
        boolean equals(Object other);

        @Override
        int hashCode();
    }
}
