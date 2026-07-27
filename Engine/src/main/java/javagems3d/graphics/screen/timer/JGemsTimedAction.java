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

package javagems3d.graphics.screen.timer;

import javagems3d.JGems3D;

public final class JGemsTimedAction {
    private boolean shouldBeErased;
    private double lastTime;
    private float deltaTime;
    private double accumulatedTime;

    JGemsTimedAction() {
        this.lastTime = JGems3D.glfwTime();
        this.shouldBeErased = false;
    }

    public void update() {
        double currentTime = JGems3D.glfwTime();
        this.deltaTime = (float) (currentTime - this.lastTime);
        this.lastTime = currentTime;
        this.accumulatedTime += this.deltaTime;
    }

    public void reset() {
        this.lastTime = JGems3D.glfwTime();
        this.accumulatedTime = 0.0f;
    }

    public void modAccumulatedTime(float modBy) {
        this.accumulatedTime %= modBy;
    }

    public void dispose() {
        this.shouldBeErased = true;
    }

    public boolean resetTimerAfterReachedSeconds(double seconds) {
        if (this.accumulatedTime >= seconds) {
            this.accumulatedTime = 0.0d;
            return true;
        }
        return false;
    }

    public boolean isShouldBeErased() {
        return this.shouldBeErased;
    }

    public double getLastTime() {
        return this.lastTime;
    }

    public float getDeltaTime() {
        return this.deltaTime;
    }

    public double getAccumulatedTime() {
        return this.accumulatedTime;
    }
}
