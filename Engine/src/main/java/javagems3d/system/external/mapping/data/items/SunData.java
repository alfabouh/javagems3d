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

import com.google.gson.*;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.lang.reflect.Type;

public class SunData implements SectionData<SunData> {
    public boolean drawSunOnSkyBox;
    public float brightness;
    public Vector3f color;
    public Vector3f position;

    private SunData() {
    }

    public SunData(boolean drawSunOnSkyBox, float brightness, Vector3f color, Vector3f position) {
        this.drawSunOnSkyBox = drawSunOnSkyBox;
        this.brightness = brightness;
        this.color = color;
        this.position = position;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<SunData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<SunData>() {
            @Override
            public JsonElement write(SunData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("drawSunOnSkyBox", context.serialize(toWrite.drawSunOnSkyBox));
                    jsonObject.add("brightness", context.serialize(toWrite.brightness));
                    jsonObject.add("color", context.serialize(toWrite.color));
                    jsonObject.add("position", context.serialize(toWrite.position));
                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public SunData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    float brightness = context.deserialize(jsonObject.get("brightness"), Float.class);
                    Vector3f color = context.deserialize(jsonObject.get("color"), Vector3f.class);
                    Vector3f position = context.deserialize(jsonObject.get("position"), Vector3f.class);
                    boolean drawSunOnSkyBox = context.deserialize(jsonObject.get("drawSunOnSkyBox"), Boolean.class);
                    return new SunData(drawSunOnSkyBox, brightness, color, position);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
