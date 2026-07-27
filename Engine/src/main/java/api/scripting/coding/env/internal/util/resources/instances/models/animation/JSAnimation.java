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

package api.scripting.coding.env.internal.util.resources.instances.models.animation;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.system.resources.assets.models.animation.Animation;
import javagems3d.system.resources.assets.models.animation.AnimationFrame;

import java.util.List;

@JSCodingClass(binding = "JSAnimation", description = "Wrapper for handling animations, including frames, duration, and FPS.")
public class JSAnimation {
    @JSHideFromDoc
    private Animation animation;

    @JSHideFromDoc
    public JSAnimation(Animation animation) {
        this.animation = animation;
    }

    @JSHideFromDoc
    public JSAnimation(String name, double duration, double fps, List<JSAnimationFrame> frameList) {
        this.animation = new Animation(name, duration, fps, frameList.stream().map(JSAnimationFrame::getJavaAnimationFrame).toList());
    }

    @JSCodingFunctionOrMethod(description = "Get animation name")
    public String name() {
        return this.animation.name();
    }

    @JSCodingFunctionOrMethod(description = "Get animation duration in seconds")
    public double duration() {
        return this.animation.duration();
    }

    @JSCodingFunctionOrMethod(description = "Get animation FPS")
    public double fps() {
        return this.animation.fps();
    }

    @JSCodingFunctionOrMethod(description = "Get list of animation frames")
    public List<JSAnimationFrame> getFrameList() {
        return this.animation.frameList().stream().map(JSAnimationFrame::new).toList();
    }

    @JSCodingFunctionOrMethod(description = "Get underlying Java animation (unsafe)")
    @JSHideFromDoc
    public Animation getJavaAnimation() {
        return this.animation;
    }
}