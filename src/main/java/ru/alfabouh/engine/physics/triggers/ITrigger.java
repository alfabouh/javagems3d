package ru.alfabouh.engine.physics.triggers;

import ru.alfabouh.engine.physics.jb_objects.JBulletEntity;

@FunctionalInterface
public interface ITrigger {
    void trigger(JBulletEntity entityTriggered);
}
