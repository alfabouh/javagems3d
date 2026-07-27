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

package javagems3d.system.resources.localisation;
import javagems3d.JGems3D;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class LocalizationManager {
    public static Lang ENGLISH = new Lang("English");
    public static Lang RUSSIAN = new Lang("Russian");

    private final Map<Lang, LangMap> laguages;
    private final Map<Integer, Lang> indexedLanguages;

    public LocalizationManager() {
        this.laguages = new LinkedHashMap<>();
        this.indexedLanguages = new HashMap<>();

        {
            this.createNewLanguage(LocalizationManager.ENGLISH);
            this.createNewLanguage(LocalizationManager.RUSSIAN);
        }
    }

    public void createNewLanguage(Lang langName) {
        this.laguages.put(langName, new LangMap(new HashMap<>()));
        this.indexedLanguages.put(this.indexedLanguages.size(), langName);
    }

    public void readLanguageMap(LocalizationManager.Lang lang, @NotNull JGemsPathSource path) throws IOException {
        LangMap langMap = this.laguages.get(lang);
        if (langMap == null) {
            Log.get().error("Language " + lang + " not found!");
        } else {
            try (InputStream inputStream = JGems3D.getInputStream(path)) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
                String line;
                int l = 0;
                while ((line = reader.readLine()) != null) {
                    if (line.isEmpty()) {
                        continue;
                    }
                    l += 1;
                    line = line.trim();
                    String[] strings = line.split("=");
                    if (strings.length == 2) {
                        langMap.stringMap().put(strings[0], strings[1]);
                    } else {
                        Log.get().warn("Error in lang files " + path + " on line: " + l);
                    }
                }
                reader.close();
                Log.get().info("Read lang: " + langMap.stringMap());
            }
        }
    }

    public String format(Lang lang, String key, Object... args) {
       LangMap langMap = this.laguages.get(lang);
        if (langMap == null || !langMap.stringMap().containsKey(key)) {
            return key;
        }
        return String.format(langMap.stringMap().get(key), args);
    }

    public Map<Integer, Lang> getIndexedLanguages() {
        return this.indexedLanguages;
    }

    public Map<Lang, LangMap> getLanguages() {
        return this.laguages;
    }

    public record LangMap(Map<String, String> stringMap) { ; }
    public record Lang(String lang) { ; }
}
