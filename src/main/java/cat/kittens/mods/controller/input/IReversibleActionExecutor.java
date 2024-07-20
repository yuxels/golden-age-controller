package cat.kittens.mods.controller.input;

public interface IReversibleActionExecutor {
    static final IReversibleActionExecutor EMPTY = ignored -> {};

    void perform(IControllerMapping.Context ctx);
}
