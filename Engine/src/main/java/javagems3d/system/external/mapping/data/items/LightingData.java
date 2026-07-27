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
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class LightingData implements SectionData<LightingData> {
    public boolean bloomEffect;
    public float exposure;
    public float gamma;
    public float ssaoRange;
    public float ssaoBias;
    public float ssaoRadius;

    private LightingData() {
    }

    public LightingData(boolean bloomEffect, float exposure, float gamma, float ssaoRange, float ssaoBias, float ssaoRadius) {
        this.bloomEffect = bloomEffect;
        this.exposure = exposure;
        this.gamma = gamma;
        this.ssaoRange = ssaoRange;
        this.ssaoBias = ssaoBias;
        this.ssaoRadius = ssaoRadius;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<LightingData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<>() {
            @Override
            public JsonElement write(LightingData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("bloomEffect", context.serialize(toWrite.bloomEffect));
                    jsonObject.add("exposure", context.serialize(toWrite.exposure));
                    jsonObject.add("gamma", context.serialize(toWrite.gamma));
                    jsonObject.add("ssaoRange", context.serialize(toWrite.ssaoRange));
                    jsonObject.add("ssaoBias", context.serialize(toWrite.ssaoBias));
                    jsonObject.add("ssaoRadius", context.serialize(toWrite.ssaoRadius));
                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public LightingData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    boolean bloomEffect = context.deserialize(jsonObject.get("bloomEffect"), boolean.class);
                    float exposure = context.deserialize(jsonObject.get("exposure"), float.class);
                    float gamma = context.deserialize(jsonObject.get("gamma"), float.class);
                    float ssaoRange = context.deserialize(jsonObject.get("ssaoRange"), float.class);
                    float ssaoBias = context.deserialize(jsonObject.get("ssaoBias"), float.class);
                    float ssaoRadius = context.deserialize(jsonObject.get("ssaoRadius"), float.class);
                    return new LightingData(bloomEffect, exposure, gamma,  ssaoRange, ssaoBias, ssaoRadius);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
