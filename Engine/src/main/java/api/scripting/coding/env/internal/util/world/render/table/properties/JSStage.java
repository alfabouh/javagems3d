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

package api.scripting.coding.env.internal.util.world.render.table.properties;

import api.scripting.coding.env.def.*;
import javagems3d.graphics.objects.rendering.pipeline.enums.Stage;
import javagems3d.graphics.objects.rendering.pipeline.enums.Type;

@JSCodingClass(binding = "JSStage", description = "Wrapper for Stage enum, providing access to stage type and predefined constants.")
public class JSStage {
    @JSCodingField(description = "Underlying Java Stage object")
    private final Stage stage;

    @JSCodingConstructor(description = "Wraps an existing Stage object", paramNames = {"stage"})
    @JSHideFromDoc
    public JSStage(Stage stage) {
        this.stage = stage;
    }

    @JSCodingField(description = "Forward rendering stage")
    public static final JSStage FORWARD = new JSStage(Stage.FORWARD);

    @JSCodingField(description = "Deferred direct rendering stage")
    public static final JSStage DEFERRED_DIRECT = new JSStage(Stage.DEFERRED_DIRECT);

    @JSCodingField(description = "Deferred indirect rendering stage")
    public static final JSStage DEFERRED_INDIRECT = new JSStage(Stage.DEFERRED_INDIRECT);

    @JSCodingField(description = "Direct shadow rendering stage")
    public static final JSStage SHADOW_DIRECT = new JSStage(Stage.SHADOW_DIRECT);

    @JSCodingField(description = "Indirect shadow rendering stage")
    public static final JSStage SHADOW_INDIRECT = new JSStage(Stage.SHADOW_INDIRECT);

    @JSCodingFunctionOrMethod(description = "Returns the type of this stage (DIRECT or INDIRECT)", paramNames = {})
    public Type getType() {
        return stage.getType();
    }

    @JSCodingFunctionOrMethod(description = "Returns the underlying Java Stage object", paramNames = {})
    public Stage getJavaStage() {
        return stage;
    }
}