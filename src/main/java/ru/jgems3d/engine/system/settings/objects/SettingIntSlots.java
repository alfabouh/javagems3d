package ru.jgems3d.engine.system.settings.objects;

import ru.jgems3d.engine.JGems;
import ru.jgems3d.engine.math.Pair;

import java.util.HashMap;
import java.util.Map;

public class SettingIntSlots extends SettingSlot {
    private final Map<Integer, Pair<String, Boolean>> intNames;

    public SettingIntSlots(String name, int defaultValue, int min, int max) {
        super(name, defaultValue, min, max);
        this.intNames = new HashMap<>();
    }

    public void addArticle(int i, String name, boolean isKeyForI18n) {
        this.intNames.put(i, new Pair<>(name, isKeyForI18n));
    }

    public String getName(int i) {
        return this.intNames.containsKey(i) ? (this.intNames.get(i).getSecond() ? JGems.get().I18n(this.intNames.get(i).getFirst()) : this.intNames.get(i).getFirst()) : Integer.toString(i);
    }

    public String getCurrentName() {
        return this.getName(this.getValue());
    }
}
