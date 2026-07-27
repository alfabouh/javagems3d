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

package api.scripting.coding.env.internal.util.world.physical.entity.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingField;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.physics.entities.properties.state.EntityState;

@JSCodingClass(binding = "JSEntityState", description = "Represents state flags of an entity.")
public class JSEntityState {
    @JSCodingField(description = "Entity is inside liquid, Bit mask. = 1 << 2") public static int IN_LIQUID = EntityState.Type.IN_LIQUID;
    @JSCodingField(description = "Entity is selected by player, Bit mask. = 1 << 3") public static int SELECTED = EntityState.Type.IS_SELECTED_BY_PLAYER;

    @JSCodingField(description = "Underlying entity state")
    private final EntityState state;

    public JSEntityState() {
        this.state = new EntityState();
    }

    @JSHideFromDoc
    public JSEntityState(EntityState state) {
        this.state = state;
    }

    @JSCodingFunctionOrMethod(description = "Clear all states")
    public void clear() {
        this.state.clear();
    }

    @JSCodingFunctionOrMethod(description = "Set state", paramNames = {"type"})
    public void setState(int type) {
        this.state.setState(type);
    }

    @JSCodingFunctionOrMethod(description = "Remove state", paramNames = {"type"})
    public void removeState(int type) {
        this.state.removeState(type);
    }

    @JSCodingFunctionOrMethod(description = "Check state", paramNames = {"type"})
    public boolean hasState(int type) {
        return this.state.checkState(type);
    }

    @JSCodingFunctionOrMethod(description = "Get raw state bits")
    public int getBits() {
        return this.state.getStateBits();
    }

    @JSCodingFunctionOrMethod(description = "Can be selected by player")
    public boolean canBeSelected() {
        return this.state.isCanBeSelectedByPlayer();
    }

    @JSCodingFunctionOrMethod(description = "Set selectable by player", paramNames = {"flag"})
    public void setSelectable(boolean flag) {
        this.state.setCanBeSelectedByPlayer(flag);
    }

    @JSCodingFunctionOrMethod(description = "Real java object")
    public EntityState getJavaState() {
        return this.state;
    }
}