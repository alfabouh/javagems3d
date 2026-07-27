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

package javagems3d.system.external.mapping.data.items;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class ShadowsData implements SectionData<ShadowsData> {
    public Vector3f splits;
    public boolean sunShadows;
    public int sunShadowRes;
    public int pointLightShadowRes;
    public int spotLightShadowRes;

    private ShadowsData() {
    }

    public ShadowsData(Vector3f splits, boolean sunShadows, int sunShadowRes, int pointLightShadowRes, int spotLightShadowRes) {
        this.splits = splits;
        this.sunShadows = sunShadows;
        this.sunShadowRes = sunShadowRes;
        this.pointLightShadowRes = pointLightShadowRes;
        this.spotLightShadowRes = spotLightShadowRes;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<ShadowsData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<>() {
            @Override
            public JsonElement write(ShadowsData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("splits", context.serialize(toWrite.splits));
                    jsonObject.add("sunShadows", context.serialize(toWrite.sunShadows));
                    jsonObject.add("sunShadowRes", context.serialize(toWrite.sunShadowRes));
                    jsonObject.add("pointLightShadowRes", context.serialize(toWrite.pointLightShadowRes));
                    jsonObject.add("spotLightShadowRes", context.serialize(toWrite.spotLightShadowRes));
                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public ShadowsData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    Vector3f splits = context.deserialize(jsonObject.get("splits"), Vector3f.class);
                    boolean sunShadows = jsonObject.has("sunShadows") ? context.deserialize(jsonObject.get("sunShadows"), boolean.class) : true;
                    int sunShadowRes = jsonObject.has("sunShadowRes") ? context.deserialize(jsonObject.get("sunShadowRes"), int.class) : JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
                    int pointLightShadowRes = jsonObject.has("pointLightShadowRes") ? context.deserialize(jsonObject.get("pointLightShadowRes"), int.class) : JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
                    int spotLightShadowRes = jsonObject.has("spotLightShadowRes") ? context.deserialize(jsonObject.get("spotLightShadowRes"), int.class) : JGemsConfig.SYSTEM.DEFAULT_MAX_SHADOW_RES;
                    return new ShadowsData(splits, sunShadows, sunShadowRes, pointLightShadowRes, spotLightShadowRes);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
