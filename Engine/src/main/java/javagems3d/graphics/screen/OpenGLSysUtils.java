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

package javagems3d.graphics.screen;

import logger.Log;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL46;
import org.lwjgl.system.MemoryUtil;

import java.nio.IntBuffer;

public abstract class OpenGLSysUtils {
    public static String validateOGLFunctions() {
        StringBuilder validationResult = new StringBuilder();

        if (!GL.getCapabilities().GL_ARB_multi_draw_indirect) {
            validationResult.append("Error: OpenGL does not support (GL_ARB_multi_draw_indirect).\n");
        }

        if (!GL.getCapabilities().GL_ARB_bindless_texture) {
            validationResult.append("Error: OpenGL does not support (GL_ARB_bindless_texture).\n");
        }

        String str = validationResult.toString();
        return str.isEmpty() ? null : str;
    }

    public static void registerOGLDebugOutput() {
        Log.get().debug("Enabled OpenGL Debug Context");
        GLFW.glfwWindowHint(GLFW.GLFW_OPENGL_DEBUG_CONTEXT, GLFW.GLFW_TRUE);
        GL46.glEnable(GL46.GL_DEBUG_OUTPUT);
        GL46.glEnable(GL46.GL_DEBUG_OUTPUT_SYNCHRONOUS);
        GL46.glDebugMessageCallback((source, type, id, severity, length, message, param) -> {
            if (severity == GL46.GL_DEBUG_SEVERITY_NOTIFICATION) {
               return;
            }
            String msgS = MemoryUtil.memUTF8(message, length);
            String sourceS = "unknown";
            String typeS = "unknown";
            String severityS = "unknown";
            switch (source) {
                case GL46.GL_DEBUG_SOURCE_API: {
                    sourceS = "OpenGL API Functions";
                    break;
                }
                case GL46.GL_DEBUG_SOURCE_WINDOW_SYSTEM: {
                    sourceS = "Window Functions";
                    break;
                }
                case GL46.GL_DEBUG_SOURCE_THIRD_PARTY: {
                    sourceS = "Third Party Functions";
                    break;
                }
                case GL46.GL_DEBUG_SOURCE_APPLICATION: {
                    sourceS = "Application";
                    break;
                }
                case GL46.GL_DEBUG_SOURCE_OTHER: {
                    sourceS = "Other";
                    break;
                }
            }
            switch (type) {
                case GL46.GL_DEBUG_TYPE_ERROR: {
                    typeS = "OpenGL API Error";
                    break;
                }
                case GL46.GL_DEBUG_TYPE_DEPRECATED_BEHAVIOR: {
                    typeS = "Deprecated Function Error";
                    break;
                }
                case GL46.GL_DEBUG_TYPE_UNDEFINED_BEHAVIOR: {
                    typeS = "Undefined Error";
                    break;
                }
                case GL46.GL_DEBUG_TYPE_PORTABILITY: {
                    typeS = "Portable Function Error";
                    break;
                }
                case GL46.GL_DEBUG_TYPE_PERFORMANCE: {
                    typeS = "Performance Warning";
                    break;
                }
                case GL46.GL_DEBUG_TYPE_MARKER: {
                    typeS = "Annotation/Marker";
                    break;
                }
                case GL46.GL_DEBUG_TYPE_PUSH_GROUP: {
                    typeS = "Push Group Stack Error";
                    break;
                }
                case GL46.GL_DEBUG_TYPE_POP_GROUP: {
                    typeS = "Pop Group Stack Error";
                    break;
                }
                case GL46.GL_DEBUG_TYPE_OTHER: {
                    typeS = "Some Error";
                    break;
                }
            }
            switch (severity) {
                case GL46.GL_DEBUG_SEVERITY_HIGH: {
                    severityS = "HIGH SEVERITY/ERROR";
                    break;
                }
                case GL46.GL_DEBUG_SEVERITY_MEDIUM: {
                    severityS = "MEDIUM SEVERITY/WARNING";
                    break;
                }
                case GL46.GL_DEBUG_SEVERITY_LOW: {
                    severityS = "WARNING";
                    break;
                }
                case GL46.GL_DEBUG_SEVERITY_NOTIFICATION: {
                    severityS = "NOTIFICATION";
                    break;
                }
            }
            Log.get().error("[OpenGL]: " + msgS + " ::: \n" + sourceS + ", (type = " + typeS + "), severity: " + severityS);
        }, 0L);
        GL46.glDebugMessageControl(GL46.GL_DONT_CARE, GL46.GL_DONT_CARE, GL46.GL_DEBUG_SEVERITY_LOW, (IntBuffer) null, true);
    }
}
