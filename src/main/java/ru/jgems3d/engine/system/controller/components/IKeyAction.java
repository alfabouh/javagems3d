package ru.jgems3d.engine.system.controller.components;

@FunctionalInterface
public interface IKeyAction {
    void onTrigger(KeyAction keyAction);

    enum KeyAction {
        PRESS,
        UN_PRESS,
        CLICK
    }
}
