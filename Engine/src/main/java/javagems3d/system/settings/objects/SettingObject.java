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

package javagems3d.system.settings.objects;

import org.jetbrains.annotations.NotNull;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

import java.io.Serializable;

public abstract class SettingObject<T extends Serializable> {
    private final String name;
    private final T defaultValue;
    private T value;

    public SettingObject(String name, @NotNull T defaultValue) {
        this.name = name;
        this.defaultValue = defaultValue;
        this.value = defaultValue;
    }

    public abstract T tryParseFromString(String string);

    public void setDefault() {
        this.value = this.getDefaultValue();
    }

    public @NotNull T getDefaultValue() {
        return this.defaultValue;
    }

    public @NotNull T getValue() {
        return this.value;
    }

    @SuppressWarnings("all")
    public void setValue(Object value) {
        if (!value.getClass().isAssignableFrom(this.value.getClass())) {
            throw new JGemsRuntimeException("Couldn't cast value");
        }
        this.value = (T) value;
    }

    public void setValue(T value) {
        this.value = value;
    }

    public String getName() {
        return this.name;
    }
}
