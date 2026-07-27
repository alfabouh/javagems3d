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

package api.scripting.coding.env.internal.util.world.physical.zones.real;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.world.physical.entity.JSWorldObjectI;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSTriggerAction;
import api.scripting.coding.env.internal.util.world.physical.zones.properties.JSZone;
import javagems3d.physics.world.IWorld;
import javagems3d.physics.world.basic.IWorldObject;
import javagems3d.physics.world.triggers.ITriggerAction;
import javagems3d.physics.world.triggers.Zone;
import javagems3d.physics.world.triggers.liquids.Liquid;

@JSCodingClass(binding = "JSRealLiquid", description = "Liquid with JS-defined behavior (inheritance-based).")
public abstract class JSRealLiquid extends Liquid implements JSWorldObjectI {
    @JSCodingField(description = "Trigger callback")
    private ITriggerAction action;

    @JSCodingField(description = "Indicates whether liquid is dead")
    private boolean dead;

    public JSRealLiquid(JSZone zone) {
        super(zone.getJavaZone());
    }

    @Override
    protected void onEntityCollideLiquid(Object e, long pointId) {
        if (this.action != null) {
            this.action.contactContinue(e, pointId);
        }
    }

    @JSCodingFunctionOrMethod(description = "Set enter callback", paramNames = {"action"})
    public void setAction(JSTriggerAction action) {
        this.action = action != null ? action.toJava() : null;
    }

    @JSCodingFunctionOrMethod(description = "Get zone")
    public JSZone getZoneJS() {
        Zone z = this.getZone();
        return new JSZone(new JSVector3f(z.location()), new JSVector3f(z.size()));
    }

    @Override
    @JSCodingFunctionOrMethod(description = "Marks liquid as dead")
    public void setDead() {
        this.dead = true;
    }

    @Override
    @JSCodingFunctionOrMethod(description = "Returns whether liquid is dead")
    public boolean isDead() {
        return this.dead;
    }

    @Override
    public void onUpdate(IWorld iWorld) {
        super.onUpdate(iWorld);
    }

    @JSCodingFunctionOrMethod(description = "Returns underlying Java object (unsafe)")
    @JSHideFromDoc
    public Liquid getJavaLiquid() {
        return this;
    }

    @JSHideFromDoc
    @Override
    public IWorldObject getJavaWorldObject() {
        return this;
    }
}