package javagems3d.physics.world.triggers;

public interface ITriggerAction {
    void contactContinue(Object userObject, long pointId);
    void contactStarted(Object userObject, long manifoldId);
    void contactEnded(Object userObject, long manifoldId);
    default boolean contractPointCreated(Object userObject, long pointID, long manifoldID) {
        return true;
    }
}
