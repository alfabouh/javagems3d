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

package javagems3d.graphics.rendering.ui.jgems_imgui.elements.base.font;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public enum FontCode {
    Window("Windows-1251"),
    Utf8("UTF-8"),
    ASCII("US-ASCII"),
    LATIN("ISO-8859-1");

    private final String getChars;

    FontCode(String code) {
        this.getChars = this.getChars(code);
    }

    public String getChars() {
        return this.getChars;
    }

    private String getChars(String code) {
        CharsetEncoder charsetEncoder = Charset.forName(code).newEncoder();
        StringBuilder result = new StringBuilder();
        for (char c = 0; c < Character.MAX_VALUE; c++) {
            if (charsetEncoder.canEncode(c)) {
                result.append(c);
            }
        }
        return result.toString();
    }
}
