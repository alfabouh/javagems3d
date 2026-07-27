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

package javagems3d.graphics.objects.rendering.attributes.base;

import javagems3d.graphics.rendering.scene.culling.rules.CullingRules;
import javagems3d.system.resources.managing.resources.data.ICopyable;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class RenderProperties implements ICopyable<RenderProperties> {
    private Map<String, RenderPropVal> propertiesMap;
    private final CullingRules cullingRules;

    public RenderProperties(CullingRules cullingRules) {
        this.cullingRules = cullingRules;
        this.propertiesMap = new HashMap<>();
    }

    public RenderProperties setValueFloat(String key, float value) {
        @Nullable FloatValue existing = this.propertiesMap.containsKey(key) ? (FloatValue) this.propertiesMap.get(key) : null;
        return this.setValueFloat(key, value, existing == null ? Float.NEGATIVE_INFINITY : existing.min, existing == null ? Float.POSITIVE_INFINITY : existing.max);
    }

    public RenderProperties setValueFloat(String key, float value, float min, float max) {
        this.propertiesMap.put(key, new FloatValue(value, min, max));
        return this;
    }

    public RenderProperties setValueBool(String key, boolean value) {
        this.propertiesMap.put(key, new BoolValue(value));
        return this;
    }

    public RenderProperties setValueInt(String key, int value) {
        @Nullable IntValue existing = this.propertiesMap.containsKey(key) ? (IntValue) this.propertiesMap.get(key) : null;
        return this.setValueInt(key, value, existing == null ? Integer.MIN_VALUE : existing.min, existing == null ? Integer.MAX_VALUE : existing.max);
    }

    public RenderProperties setValueInt(String key, int value, int min, int max) {
        this.propertiesMap.put(key, new IntValue(value, min, max));
        return this;
    }

    public boolean has(String key) {
        return this.propertiesMap.containsKey(key);
    }

    public float getFloat(String key, float defaultV) {
        RenderPropVal val = this.propertiesMap.get(key);
        if (val instanceof FloatValue f) {
            return f.value;
        }
        return defaultV;
    }

    public boolean getBool(String key, boolean defaultV) {
        RenderPropVal val = this.propertiesMap.get(key);
        if (val instanceof BoolValue b) {
            return b.value;
        }
        return defaultV;
    }

    public int getInt(String key, int defaultV) {
        RenderPropVal val = this.propertiesMap.get(key);
        if (val instanceof IntValue i) {
            return i.value;
        }
        return defaultV;
    }

    public FloatValue getFloatValue(String key, float defaultV) {
        RenderPropVal val = this.propertiesMap.get(key);
        if (val instanceof FloatValue f) {
            return f;
        }
        return new FloatValue(defaultV, 0.0f, 0.0f);
    }

    public IntValue getIntValue(String key, int defaultV) {
        RenderPropVal val = this.propertiesMap.get(key);
        if (val instanceof IntValue i) {
            return i;
        }
        return new IntValue(defaultV, 0, 0);
    }

    public BoolValue getBoolValue(String key, boolean defaultV) {
        RenderPropVal val = this.propertiesMap.get(key);
        if (val instanceof BoolValue b) {
            return b;
        }
        return new BoolValue(defaultV);
    }

    public CullingRules getCullingRules() {
        return this.cullingRules;
    }

    protected void setPropertiesMap(@NotNull Map<String, RenderPropVal> map) {
        this.propertiesMap = map;
    }

    public Map<String, RenderPropVal> getPropertiesMap() {
        return this.propertiesMap;
    }

    public Map<String, RenderPropVal> copyPropertiesMap() {
        return new HashMap<>(this.propertiesMap);
    }

    @Override
    public RenderProperties copy() {
        RenderProperties renderProperties = new RenderProperties(this.getCullingRules());
        renderProperties.setPropertiesMap(this.copyPropertiesMap());
        return renderProperties;
    }

    public static class RenderPropVal {
    }

    public static class FloatValue extends RenderPropVal {
        public float value;
        public final float min;
        public final float max;

        public FloatValue(float value, float min, float max) {
            this.value = value;
            this.min = min;
            this.max = max;
        }
    }

    public static class IntValue extends RenderPropVal {
        public int value;
        public final int min;
        public final int max;

        public IntValue(int value, int min, int max) {
            this.value = value;
            this.min = min;
            this.max = max;
        }
    }

    public static class BoolValue extends RenderPropVal {
        public boolean value;

        public BoolValue(boolean value) {
            this.value = value;
        }
    }
}