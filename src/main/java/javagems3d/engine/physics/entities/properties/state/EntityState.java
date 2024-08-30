/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.physics.entities.properties.state;

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

    public void removeState(Type stateBit) {
        this.stateBits = this.getStateBits() & ~stateBit.getState();
    }

    public void setState(Type stateBit) {
        this.stateBits = this.getStateBits() | stateBit.getState();
    }

    public boolean checkState(Type stateBit) {
        return (this.getStateBits() & stateBit.getState()) != 0;
    }

    public boolean isCanBeSelectedByPlayer() {
        return this.canBeSelectedByPlayer;
    }

    public void setCanBeSelectedByPlayer(boolean canBeSelectedByPlayer) {
        this.canBeSelectedByPlayer = canBeSelectedByPlayer;
    }

    public enum Type {
        IN_LIQUID(1 << 2),
        IS_SELECTED_BY_PLAYER(1 << 3);

        private final int state;

        Type(int i) {
            this.state = i;
        }

        public int getState() {
            return this.state;
        }
    }
}