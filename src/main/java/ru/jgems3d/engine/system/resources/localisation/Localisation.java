package ru.jgems3d.engine.system.resources.localisation;

import ru.jgems3d.engine.JGems3D;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.misc.JGPath;
import ru.jgems3d.exceptions.JGemsException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class Localisation {
    public static Lang defaultSystemLang = Lang.English;

    static {
        Locale defaultLocale = Locale.getDefault();
        String defLang = defaultLocale.getDisplayLanguage(Locale.ENGLISH);
        Localisation.defaultSystemLang = Lang.getLangByName(defLang);
    }

    private Lang currentlang;
    private LangMap currentLangTable;

    public Localisation() {
        this.currentlang = null;
    }

    public static Lang createLocalisation(String langName, JGPath path) {
        if (Lang.checkLangInSet(langName)) {
            Lang l = Lang.getLangByName(langName);
            Localisation.setLangLocalisationPath(l, path);
            return l;
        } else {
            return Lang.createLang(langName, path);
        }
    }

    public static void setLangLocalisationPath(Lang lang, JGPath path) {
        lang.setFilePath(path);
        if (JGemsHelper.getLocalisation().getCurrentlang().equals(lang)) {
            JGemsHelper.getLocalisation().setLanguage(lang);
        }
    }

    public void setLanguage(Lang lang) {
        if (lang == null) {
            JGemsHelper.getLogger().warn("Tried to set NULL language");
            lang = Lang.English;
        }
        JGemsHelper.getLogger().log("Loading language table: " + lang.getFullName());
        this.readLangFileInTable(lang);
        JGemsHelper.getLogger().log("Language table loaded");
        this.currentlang = lang;
    }

    private void readLangFileInTable(Lang lang) {
        LangMap langMap = new LangMap();
        try {
            this.readStream(langMap, new JGPath(lang.getFilePath(), (lang.getFullName().toLowerCase() + ".lang")));
        } catch (IOException e) {
            throw new JGemsException(e);
        }
        this.currentLangTable = langMap;
    }

    private void readStream(LangMap langMap, JGPath filePath) throws IOException {
        try (InputStream inputStream = JGems3D.loadFileJarSilently(filePath)) {
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
                    JGemsHelper.getLogger().warn("Error in lang file " + filePath + " on line: " + l);
                }
            }
            reader.close();
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
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
