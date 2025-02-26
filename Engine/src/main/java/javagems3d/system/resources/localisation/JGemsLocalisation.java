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

package javagems3d.system.resources.localisation;

import javagems3d.JGems3D;
import javagems3d.help.JGemsLocalisationHelper;
import javagems3d.system.service.exceptions.JGemsIOException;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class JGemsLocalisation {
    public static Lang defaultSystemLang = Lang.DefaultEnglish;

    static {
        Locale defaultLocale = Locale.getDefault();
        String defLang = defaultLocale.getDisplayLanguage(Locale.ENGLISH);
        JGemsLocalisation.defaultSystemLang = Lang.getLangByName(defLang);
    }

    private Lang currentlang;
    private LangMap currentLangTable;

    public JGemsLocalisation() {
        this.currentlang = null;
    }

    public static Lang createLocalisation(String langName, JGemsPath path) {
        if (Lang.checkLangInSet(langName)) {
            Lang l = Lang.getLangByName(langName);
            JGemsLocalisation.setLangLocalisationPath(l, path);
            return l;
        } else {
            return Lang.createLang(langName, path);
        }
    }

    public static void setLangLocalisationPath(Lang lang, JGemsPath path) {
        lang.setFileDirectoryPath(path);
        if (JGemsLocalisationHelper.getLocalisation().getCurrentlang() != null && JGemsLocalisationHelper.getLocalisation().getCurrentlang().equals(lang)) {
            JGemsLocalisationHelper.getLocalisation().setLanguage(lang);
        }
    }

    public void setLanguage(Lang lang) {
        if (lang == null) {
            Log.get().warn("Tried to set NULL language");
            lang = Lang.DefaultEnglish;
        }
        this.readLangFileInTable(lang);
        Log.get().info("Initialized language table "  + lang.getFullName());
        this.currentlang = lang;
    }

    private void readLangFileInTable(Lang lang) {
        LangMap langMap = new LangMap();
        try {
            this.readStream(langMap, new JGemsPath(lang.getFileDirectoryPath(), (lang.getFullName().toLowerCase() + ".lang")));
        } catch (IOException e) {
            throw new JGemsIOException(e);
        }
        this.currentLangTable = langMap;
    }

    private void readStream(LangMap langMap, JGemsPath filePath) throws IOException {
        try (InputStream inputStream = JGems3D.loadFileFromJar(filePath)) {
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
                    langMap.addPair(strings[0], strings[1]);
                } else {
                    Log.get().warn("Error in lang path " + filePath + " on line: " + l);
                }
            }
            reader.close();
        }
    }

    public String format(String key) {
        LangMap langMap = this.getCurrentLangTable();
        if (langMap == null || !langMap.isKeyExist(key)) {
            return key;
        }
        return langMap.getValue(key);
    }

    public LangMap getCurrentLangTable() {
        return this.currentLangTable;
    }

    public Lang getCurrentlang() {
        return this.currentlang;
    }
}
