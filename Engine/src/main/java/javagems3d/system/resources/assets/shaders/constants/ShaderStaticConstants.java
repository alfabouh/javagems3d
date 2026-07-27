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

package javagems3d.system.resources.assets.shaders.constants;

import java.util.HashMap;
import java.util.Map;

public final class ShaderStaticConstants {
    private final Map<String, String> cnstMap;

    public ShaderStaticConstants() {
        this.cnstMap = new HashMap<>();
    }

    public void createConstant(String key, String value) {
        this.getCnstMap().put(key.replaceAll("CONST.", ""), value);
    }

    public String getValue(String key) {
        return this.getCnstMap().get(key);
    }

    public void clear() {
        this.getCnstMap().clear();
    }

    public Map<String, String> getCnstMap() {
        return this.cnstMap;
    }
}