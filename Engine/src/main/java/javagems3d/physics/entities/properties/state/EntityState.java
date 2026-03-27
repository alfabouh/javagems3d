package javagems3d.physics.entities.properties.state;

public class EntityState {
    private int stateBits;
    private boolean canBeSelectedByPlayer;

    public EntityState() {
        this.clear();
        this.canBeSelectedByPlayer = false;
    }

    public void clear() {
        this.stateBits = 0;
    }

    public int getStateBits() {
        return this.stateBits;
    }

    public void removeState(int stateBit) {
        this.stateBits = this.getStateBits() & ~stateBit;
    }

    public void setState(int stateBit) {
        this.stateBits = this.getStateBits() | stateBit;
    }

    public boolean checkState(int stateBit) {
        return (this.getStateBits() & stateBit) != 0;
    }

    public boolean isCanBeSelectedByPlayer() {
        return this.canBeSelectedByPlayer;
    }

    public void setCanBeSelectedByPlayer(boolean canBeSelectedByPlayer) {
        this.canBeSelectedByPlayer = canBeSelectedByPlayer;
    }

    public static class Type {
        public static final int IN_LIQUID = (1 << 2);
        public static final int IS_SELECTED_BY_PLAYER = (1 << 3);
    }
}