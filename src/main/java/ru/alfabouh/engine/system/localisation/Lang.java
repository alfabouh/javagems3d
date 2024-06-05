package ru.alfabouh.engine.system.localisation;

public enum Lang {
    English("English", "english.lang"),
    Russian("Russian", "russian.lang");

    private final String fullName;
    private final String fileName;

    Lang(String fullName, String fileName) {
        this.fullName = fullName;
        this.fileName = fileName;
    }

    public String getFullName() {
        return this.fullName;
    }

    public String getFileName() {
        return this.fileName;
    }

    public static Lang getLangByName(String name) {
        for (Lang lang : Lang.values()) {
            if (lang.getFullName().equals(name)) {
                return lang;
            }
        }
        return Lang.English;
    }
}