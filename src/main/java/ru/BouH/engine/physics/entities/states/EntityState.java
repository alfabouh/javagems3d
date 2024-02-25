package ru.BouH.engine.physics.entities.states;

public class EntityState {
    private int stateBits;

    public EntityState() {
        this.clear();
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
        return (this.getStateBits() & stateBits) != 0;
    }

    public enum StateType {
        IN_WATER(1 << 2);

        private final int state;
        StateType(int i) {
            this.state = i;
        }

        public int getState() {
            return this.state;
        }
    }
}
