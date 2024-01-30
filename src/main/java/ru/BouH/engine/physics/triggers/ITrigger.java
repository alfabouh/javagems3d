package ru.BouH.engine.physics.triggers;

import ru.BouH.engine.physics.world.object.CollidableWorldItem;

@FunctionalInterface
public interface ITrigger {
    void trigger(CollidableWorldItem entityTriggered);
}
