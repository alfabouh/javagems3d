package ru.BouH.engine.render.scene.gui.font;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

public enum FontCode {
    Window("Windows-1251"),
    Utf("UTF-8");

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
