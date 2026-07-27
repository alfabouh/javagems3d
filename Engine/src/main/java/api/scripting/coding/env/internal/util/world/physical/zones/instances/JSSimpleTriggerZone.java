/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

package api.scripting.coding.env.internal.util.world.physical.zones.instances;

import api.scripting.coding.env.def.*;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.JSPhysicsWorld;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import api.scripting.coding.env.internal.util.world.physical.entity.properties.JSCollisionType;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSTriggerAction;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSZone;
import javagems3d.physics.entities.properties.collision.CollisionType;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.zones.SimpleTriggerZone;

@JSCodingClass(binding = "JSSimpleTriggerZone", description = "Wrapper around simple trigger zone.")
public class JSSimpleTriggerZone implements JSWorldObjectI {
    @JSCodingField(description = "Underlying trigger zone (Java side)")
    private final SimpleTriggerZone zone;

    @JSCodingConstructor(description = "Create a trigger zone from JSZone", paramNames = {"zone"})
    public JSSimpleTriggerZone(JSZone zone) {
        this.zone = new SimpleTriggerZone(zone.getJavaZone());
    }

    @JSCodingConstructor(description = "Wrap existing SimpleTriggerZone instance", paramNames = {"zone"})
    public JSSimpleTriggerZone(SimpleTriggerZone zone) {
        this.zone = zone;
    }

    @JSCodingFunctionOrMethod(description = "Get zone")
    public JSZone getZone() {
        Zone z = this.zone.getZone();
        return new JSZone(new JSVector3f(z.location()), new JSVector3f(z.size()));
    }

    @JSCodingFunctionOrMethod(description = "Set location", paramNames = {"location"})
    public void setLocation(JSVector3f location) {
        this.zone.setLocation(location.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Set trigger callback", paramNames = {"action"})
    public void setAction(JSTriggerAction action) {
        this.zone.setTriggerAction(action != null ? action.toJava() : null);
    }

    @JSCodingFunctionOrMethod(description = "Check if trigger is valid")
    public boolean isValid() {
        return this.zone.isValid();
    }

    @JSCodingFunctionOrMethod(description = "Set collision group", paramNames = {"types"})
    public void setCollisionGroup(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            arr[i] = types[i].getJavaType();
        }
        this.zone.setCollisionGroup(arr);
    }

    @JSCodingFunctionOrMethod(description = "Set collision filter", paramNames = {"types"})
    public void setCollisionFilter(JSCollisionType... types) {
        CollisionType[] arr = new CollisionType[types.length];
        for (int i = 0; i < types.length; i++) {
            arr[i] = types[i].getJavaType();
        }
        this.zone.setCollideWithGroups(arr);
    }

    @JSCodingFunctionOrMethod(description = "Spawn trigger in world", paramNames = {"world"})
    public void spawn(JSPhysicsWorld world) {
        this.zone.onSpawn(world.getJavaPhysicsWorld());
    }

    @JSCodingFunctionOrMethod(description = "Destroy trigger", paramNames = {"world"})
    public void destroy(JSPhysicsWorld world) {
        this.zone.onDestroy(world.getJavaPhysicsWorld());
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public SimpleTriggerZone getJavaTriggerZone() {
        return this.zone;
    }

    @JSCodingFunctionOrMethod(description = "Real java object")    @Override
    public IWorldObject getJavaWorldObject() {
        return this.zone;
    }
}