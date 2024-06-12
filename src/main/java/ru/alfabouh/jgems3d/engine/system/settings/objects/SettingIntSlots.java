package ru.alfabouh.jgems3d.engine.system.settings.objects;

import ru.alfabouh.jgems3d.engine.JGems;
import ru.alfabouh.jgems3d.engine.math.Pair;
import ru.alfabouh.jgems3d.engine.system.settings.basic.SettingSlot;

import java.util.HashMap;
import java.util.Map;

public class SettingIntSlots extends SettingSlot {
    private final Map<Integer, Pair<String, Boolean>> intNames;

    public SettingIntSlots(String name, Object... value) {
        super(name, value);
        this.intNames = new HashMap<>();
    }

    public void addName(int i, String name, boolean isKeyForI18n) {
        this.intNames.put(i, new Pair<>(name, isKeyForI18n));
    }

    public String getName(int i) {
        return this.intNames.containsKey(i) ? (this.intNames.get(i).getValue() ? JGems.get().I18n(this.intNames.get(i).getKey()) : this.intNames.get(i).getKey()) : Integer.toString(i);
    }

    public String getCurrentName() {
        return this.getName(this.getValue());
    }
}
