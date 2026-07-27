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

package api.scripting.coding.env.internal.util.world.physical.zones.properties;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import javagems3d.physics.world.triggers.ITriggerAction;

@JSCodingClass(binding = "JSContactAction", description = "Callbacks executed for physics contact events.")
public interface JSTriggerAction {

    @JSCodingFunctionOrMethod(description = "Called when a contact starts.", paramNames = {"object"})
    default void contactStarted(Object object, long pointId) {
    }

    @JSCodingFunctionOrMethod(description = "Called while objects remain in contact.", paramNames = {"object"})
    default void contactContinue(Object object, long manifoldId) {
    }

    @JSCodingFunctionOrMethod(description = "Called when a contact ends.", paramNames = {"object"})
    default void contactEnded(Object object, long manifoldId) {
    }

    @JSCodingFunctionOrMethod(description = "Called when a contact point is created. Return false to reject the contact.", paramNames = {"object", "pointID", "manifoldID"})
    default boolean contactPointCreated(Object object, long pointID, long manifoldID) {
        return true;
    }

    @JSHideFromDoc
    default ITriggerAction toJava() {
        return new ITriggerAction() {
            @Override
            public void contactContinue(Object userObject, long pointId) {
                JSTriggerAction.this.contactContinue(userObject, pointId);
            }

            @Override
            public void contactStarted(Object userObject, long manifoldID) {
                JSTriggerAction.this.contactStarted(userObject, manifoldID);
            }

            @Override
            public void contactEnded(Object userObject, long manifoldID) {
                JSTriggerAction.this.contactEnded(userObject, manifoldID);
            }

            @Override
            public boolean contractPointCreated(Object userObject, long pointID, long manifoldID) {
                return JSTriggerAction.this.contactPointCreated(userObject, pointID, manifoldID);
            }
        };
    }
}