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
import javagems3d.graphics.rendering.programs.textures.base.ICubeMapProgram;
import javagems3d.system.service.args.ArbitraryArguments;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.files.json.JSONFileManaging;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;

public class SkyData implements SectionData<SkyData> {
    private final String nameId;
    public float backGroundScaling;

    public SkyData(@NotNull String nameId, float backGroundScaling) {
        this.nameId = nameId;
        this.backGroundScaling = backGroundScaling;
    }

    public String getNameId() {
        return this.nameId;
    }

    @Override
    public JSONFileManaging.@NotNull SerializationRules<SkyData> getSerializationRules() {
        return new JSONFileManaging.SerializationRules<SkyData>() {
            @Override
            public JsonElement write(SkyData toWrite, Type typeOfSrc, JsonSerializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.add("nameId", context.serialize(toWrite.nameId));
                    jsonObject.add("backGroundScaling", context.serialize(toWrite.backGroundScaling));
                    return jsonObject;
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't write: " + toWrite.getClass(), e);
                }
            }

            @Override
            public SkyData read(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context, @Nullable ArbitraryArguments metaData) throws JGemsIOException {
                try {
                    JsonObject jsonObject = jsonElement.getAsJsonObject();
                    final float backGroundScaling = context.deserialize(jsonObject.get("backGroundScaling"), Float.class);
                    final String nameId = context.deserialize(jsonObject.get("nameId"), String.class);
                    return new SkyData(nameId, backGroundScaling);
                } catch (Exception e) {
                    throw new JGemsIOException("Couldn't read: " + typeOfT, e);
                }
            }
        };
    }
}
