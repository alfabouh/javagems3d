package ru.alfabouh.engine.game.settings;

import ru.alfabouh.engine.game.Game;

public class SettingTrueFalse extends SettingObject {
    private boolean flag;

    public SettingTrueFalse(String name, Object... value) {
        super(name, value);
        this.flag = (boolean) value[0];
    }

    public boolean isFlag() {
        return this.flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
        this.performSetting();
    }

    @Override
    public void performSetting(Object... objects) {
        Game.getGame().getGameSettings().saveOptions();
    }
}