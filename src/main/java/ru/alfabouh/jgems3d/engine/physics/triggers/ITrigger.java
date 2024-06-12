package ru.alfabouh.jgems3d.engine.physics.triggers;

import ru.alfabouh.jgems3d.engine.physics.jb_objects.JBulletEntity;

@FunctionalInterface
public interface ITrigger {
    void trigger(JBulletEntity entityTriggered);
}
