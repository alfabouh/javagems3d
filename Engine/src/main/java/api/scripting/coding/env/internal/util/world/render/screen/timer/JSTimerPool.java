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
import javagems3d.graphics.screen.timer.TimerPool;

@JSCodingClass(binding = "JSTimerPool", description = "Wrapper for a TimerPool, allowing creation, deletion, and management of timed actions in scripting.")
public class JSTimerPool {
    @JSHideFromDoc public final TimerPool timerPool;

    @JSHideFromDoc
    public JSTimerPool(TimerPool timerPool) {
        this.timerPool = timerPool;
    }

    @JSCodingFunctionOrMethod(description = "Create a new timed action managed by this timer pool.", paramNames = {})
    public JSTimedAction createTimer() {
        return new JSTimedAction(this.timerPool.createTimer());
    }

    @JSCodingFunctionOrMethod(description = "Delete a previously created timed action.", paramNames = {"timedAction"})
    public void deleteTimer(JSTimedAction timedAction) {
        timedAction.dispose();
    }

    @JSCodingFunctionOrMethod(description = "Clear all timers in this timer pool.", paramNames = {})
    public void clear() {
        this.timerPool.clear();
    }
}