package ru.alfabouh.jgems3d.engine.system.resources.localisation;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.logger.SystemLogging;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class Localisation {
    public static Lang defaultLang = Lang.English;

    static {
        Locale defaultLocale = Locale.getDefault();
        String languageEnglish = defaultLocale.getDisplayLanguage(Locale.ENGLISH);
        Localisation.defaultLang = Lang.getLangByName(languageEnglish);
    }

    private Lang currentlang;
    private LangMap currentLangTable;

    public Localisation() {
        this.currentlang = null;
    }

    public void setCurrentLang(Lang currentlang) {
        if (currentlang == null) {
            SystemLogging.get().getLogManager().warn("Tried to set NULL language");
            currentlang = Lang.English;
        }
        SystemLogging.get().getLogManager().log("Loading language table: " + currentlang.getFullName());
        this.readLangFilesInTable(currentlang);
        SystemLogging.get().getLogManager().log("Language table loaded");
        this.currentlang = currentlang;
    }

    private void readLangFilesInTable(Lang lang) {
        LangMap langMap = new LangMap();
        try {
            this.readStream(langMap, lang.getFileName());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.currentLangTable = langMap;
    }

    private void readStream(LangMap langMap, String fileName) throws IOException {
        try (InputStream inputStream = JGems.loadFileJarSilently("/assets/jgems/lang/" + fileName)) {
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
                    SystemLogging.get().getLogManager().warn("Error in lang file " + fileName + " on line: " + l);
                }
            }
            reader.close();
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
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
