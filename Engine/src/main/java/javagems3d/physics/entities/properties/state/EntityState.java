/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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