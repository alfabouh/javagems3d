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

package api.scripting.coding.env.internal.util.management;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSGlobalVarFactory;
import api.scripting.coding.env.def.JSHideFromDoc;
import logger.Log;

@JSCodingClass(binding = "JSLogger", description = "Logging tools.")
public class JSLogger implements JSGlobalVarFactory<JSLogger> {
    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the INFO level.", paramNames = {"message"})
    public void info(String message) {
        Log.get().info(message);
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the ERROR level.", paramNames = {"message"})
    public void error(String message) {
        Log.get().error(message);
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the WARN level.", paramNames = {"message"})
    public void warn(String message) {
        Log.get().warn(message);
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the TRACE level.", paramNames = {"message"})
    public void trace(String message) {
        Log.get().trace(message);
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the DEBUG level.", paramNames = {"message"})
    public void debugMsg(String message) {
        Log.get().debug(message);
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the INFO level.", paramNames = {"obj"})
    public void info(Object obj) {
        Log.get().info(obj == null ? "NULL" : obj.toString());
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the ERROR level.", paramNames = {"obj"})
    public void error(Object obj) {
        Log.get().error(obj == null ? "NULL" : obj.toString());
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the WARN level.", paramNames = {"obj"})
    public void warn(Object obj) {
        Log.get().warn(obj == null ? "NULL" : obj.toString());
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the TRACE level.", paramNames = {"obj"})
    public void trace(Object obj) {
        Log.get().trace(obj == null ? "NULL" : obj.toString());
    }

    @JSCodingFunctionOrMethod(description = "Logs a message with parameters at the DEBUG level.", paramNames = {"obj"})
    public void debugMsg(Object obj) {
        Log.get().debug(obj == null ? "NULL" : obj.toString());
    }

    @JSHideFromDoc
    @Override
    public JSLogger newGlobalVar() {
        return new JSLogger();
    }

    @JSHideFromDoc
    @Override
    public String getVarName() {
        return "Js_Log";
    }
}
