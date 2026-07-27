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

package javagems3d.graphics.rendering.programs.shaders;

import javagems3d.help.JGemsHelper;
import logger.Log;
import org.lwjgl.opengl.GL46;
import javagems3d.system.resources.assets.shaders.base.ShaderObject;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

public class GShaderProgram implements IShaderProgram {
    private final int programId;
    private int vertexShaderId;
    private int fragmentShaderId;
    private int geometricShaderId;
    private int tessellationControlShaderId;
    private int tessellationEvaluationShaderId;

    public GShaderProgram() {
        this.programId = GL46.glCreateProgram();
        if (this.programId == 0) {
            throw new JGemsRuntimeException("Could not create shader program");
        }
    }

    public boolean createShader(ShaderObject fragShaderObject, ShaderObject vertShaderObject, ShaderObject geomShaderObject, ShaderObject tessControlShaderObject, ShaderObject tessEvaluationShaderObject) {
        boolean flag = false;
        if (fragShaderObject != null) {
            this.createFragmentShader(fragShaderObject.getShaderText());
            flag = true;
        }
        if (vertShaderObject != null) {
            this.createVertexShader(vertShaderObject.getShaderText());
            flag = true;
        }
        if (geomShaderObject != null) {
            this.createGeometricShader(geomShaderObject.getShaderText());
            flag = true;
        }
        if (tessControlShaderObject != null && tessEvaluationShaderObject != null) {
            this.createTessellationShaders(tessControlShaderObject.getShaderText(), tessEvaluationShaderObject.getShaderText());
            flag = true;
        }
        return flag;
    }

    public void createVertexShader(String shader) {
        this.vertexShaderId = this.createShader(shader, GL46.GL_VERTEX_SHADER);
    }

    public void createFragmentShader(String shader) {
        this.fragmentShaderId = this.createShader(shader, GL46.GL_FRAGMENT_SHADER);
    }

    public void createGeometricShader(String shader) {
        this.geometricShaderId = this.createShader(shader, GL46.GL_GEOMETRY_SHADER);
    }

    public void createTessellationShaders(String shaderC, String shaderE) {
        this.tessellationControlShaderId = this.createShader(shaderC, GL46.GL_TESS_CONTROL_SHADER);
        this.tessellationEvaluationShaderId = this.createShader(shaderE, GL46.GL_TESS_EVALUATION_SHADER);
    }

    private int createShader(String shader, int type) {
        int id = GL46.glCreateShader(type);
        if (id == 0) {
            throw new JGemsRuntimeException("Could not create Shader: " + type);
        }
        GL46.glShaderSource(id, shader);
        GL46.glCompileShader(id);
        if (GL46.glGetShaderi(id, GL46.GL_COMPILE_STATUS) == 0) {
            Log.get().warn(JGemsHelper.files().getTextWithLines(shader));
            throw new JGemsRuntimeException("Compile shader error: " + GL46.glGetShaderInfoLog(id, 4096));
        }
        GL46.glAttachShader(this.programId, id);
        return id;
    }

    public boolean link() {
        GL46.glLinkProgram(this.programId);
        if (GL46.glGetProgrami(this.programId, GL46.GL_LINK_STATUS) == 0) {
            String err = GL46.glGetShaderInfoLog(this.programId, 4096);
            if (err.isEmpty()) {
                err = "UNKNOWN ERR";
            }
            Log.get().warn("Could not link shader: " + err);
            //return false;
        }
        if (this.vertexShaderId != 0) {
            GL46.glDetachShader(this.programId, this.vertexShaderId);
        }
        if (this.fragmentShaderId != 0) {
            GL46.glDetachShader(this.programId, this.fragmentShaderId);
        }
        if (this.geometricShaderId != 0) {
            GL46.glDetachShader(this.programId, this.geometricShaderId);
        }
        if (this.tessellationControlShaderId != 0) {
            GL46.glDetachShader(this.programId, this.tessellationControlShaderId);
        }
        if (this.tessellationEvaluationShaderId != 0) {
            GL46.glDetachShader(this.programId, this.tessellationEvaluationShaderId);
        }
        GL46.glValidateProgram(this.programId);
        if (GL46.glGetProgrami(this.programId, GL46.GL_VALIDATE_STATUS) == 0) {
            String err = GL46.glGetShaderInfoLog(this.programId, 4096);
            if (!err.isEmpty()) {
                Log.get().warn("Could not validate shader " + err);
                return false;
            }
        }
        return true;
    }

    public int getProgramId() {
        return this.programId;
    }
}
