package ru.alfabouh.jgems3d.engine.physics.objects.states;

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

    public void removeState(StateType stateBit) {
        this.stateBits = this.getStateBits() & ~stateBit.getState();
    }

    public void setState(StateType stateBit) {
        this.stateBits = this.getStateBits() | stateBit.getState();
    }

    public boolean checkState(StateType stateBit) {
        return (this.getStateBits() & stateBit.getState()) != 0;
    }

    public boolean isCanBeSelectedByPlayer() {
        return this.canBeSelectedByPlayer;
    }

    public void setCanBeSelectedByPlayer(boolean canBeSelectedByPlayer) {
        this.canBeSelectedByPlayer = canBeSelectedByPlayer;
    }

    public enum StateType {
        IN_WATER(1 << 2),
        IS_SELECTED_BY_PLAYER(1 << 3);

        private final int state;

        StateType(int i) {
            this.state = i;
        }

        public int getState() {
            return this.state;
        }
    }
}
