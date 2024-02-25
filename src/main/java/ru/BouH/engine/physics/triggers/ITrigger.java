package ru.BouH.engine.physics.triggers;

import ru.BouH.engine.physics.jb_objects.JBulletEntity;

@FunctionalInterface
public interface ITrigger {
    void trigger(JBulletEntity entityTriggered);
}
