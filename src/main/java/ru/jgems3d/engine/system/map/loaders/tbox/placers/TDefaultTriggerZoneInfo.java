/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 */

package ru.jgems3d.engine.system.map.loaders.tbox.placers;

import ru.jgems3d.engine.physics.world.triggers.ITriggerAction;

/**
 * This class represents trigger information for an object inside the engine itself
 */
public final class TDefaultTriggerZoneInfo {
    private final ITriggerAction triggerAction;

    public TDefaultTriggerZoneInfo(ITriggerAction triggerAction) {
        this.triggerAction = triggerAction;
    }

    public ITriggerAction getTriggerAction() {
        return this.triggerAction;
    }
}