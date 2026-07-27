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

package api.scripting.coding.env.internal.util.world.render.screen.timer;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.graphics.screen.timer.JGemsTimedAction;

@JSCodingClass(binding = "JSTimedAction", description = "Represents a timed action with methods to reset, disposal, and querying elapsed time.")
public class JSTimedAction {
    public final JGemsTimedAction timer;

    @JSHideFromDoc
    public JSTimedAction(JGemsTimedAction timer) {
        this.timer = timer;
    }

    @JSCodingFunctionOrMethod(description = "Reset the timer to its initial state.")
    public void reset() {
        this.timer.reset();
    }

    @JSCodingFunctionOrMethod(description = "Dispose of the timer and release resources.")
    public void dispose() {
        this.timer.dispose();
    }

    @JSCodingFunctionOrMethod(description = "Reset the timer if the specified number of seconds has elapsed.", paramNames = {"seconds"})
    public boolean resetTimerAfterReachedSeconds(double seconds) {
        return this.timer.resetTimerAfterReachedSeconds(seconds);
    }

    @JSCodingFunctionOrMethod(description = "Check if the timer should be erased.")
    public boolean isShouldBeErased() {
        return this.timer.isShouldBeErased();
    }

    @JSCodingFunctionOrMethod(description = "Get the last recorded time of the timer.")
    public double getLastTime() {
        return this.timer.getLastTime();
    }

    @JSCodingFunctionOrMethod(description = "Get the delta time since the last update of the timer.")
    public float getDeltaTime() {
        return this.timer.getDeltaTime();
    }

    @JSCodingFunctionOrMethod(description = "Get the total accumulated time tracked by the timer.")
    public double getAccumulatedTime() {
        return this.timer.getAccumulatedTime();
    }
}