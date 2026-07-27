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

package api.application.workbench.resources.data.wbench.properties;

import javagems3d.graphics.objects.rendering.attributes.JGemsRenderProperties;
import javagems3d.graphics.objects.rendering.attributes.base.RenderProperties;
import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;

public class WBenchRenderProperties extends RenderProperties {
    public WBenchRenderProperties() {
        super(CullingRules.get());
        this.setDefaults();
    }

    public static WBenchRenderProperties getDefault() {
        return new WBenchRenderProperties();
    }

    protected void setDefaults() {
        this.setValueInt(JGemsRenderProperties.KEY_GBUFFER_DECAL_LAYER_ID, -1, -1, Short.MAX_VALUE * 2);
        this.setValueFloat(JGemsRenderProperties.KEY_RENDER_DISTANCE, -1.0f, -1.0f, 1024.0f);
        this.setValueFloat(JGemsRenderProperties.KEY_ALPHA_DISCARD, 0.0f, 0.0f, 1.0f);
        this.setValueBool(JGemsRenderProperties.KEY_SHADOW_CASTER, true);
        this.setValueBool(JGemsRenderProperties.KEY_ALLOW_MOVEMENT_INTERPOLATION, true);
    }
}
