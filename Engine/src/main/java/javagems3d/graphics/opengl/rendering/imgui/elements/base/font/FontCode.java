/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.opengl.rendering.imgui.elements.base.font;

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
