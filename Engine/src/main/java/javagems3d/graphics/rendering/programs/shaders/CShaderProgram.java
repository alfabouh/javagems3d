/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.graphics.rendering.programs.shaders;

import logger.Log;
import org.lwjgl.opengl.GL46;
import javagems3d.system.resources.assets.shaders.base.ShaderObject;
import javagems3d.system.service.exceptions.JGemsRuntimeException;

public class CShaderProgram implements IShaderProgram {
    private final int programId;
    private int computeShaderId;

    public CShaderProgram() {
        this.programId = GL46.glCreateProgram();
        if (this.programId == 0) {
            throw new JGemsRuntimeException("Could not create shader program");
        }
    }

    public boolean createShader(ShaderObject compShaderObject) {
        if (compShaderObject == null) {
            return false;
        }
        this.createComputeShader(compShaderObject.getShaderText());
        return true;
    }

    public void createComputeShader(String shader) {
        this.computeShaderId = this.createShader(shader, GL46.GL_COMPUTE_SHADER);
    }

    private int createShader(String shader, int type) {
        int id = GL46.glCreateShader(type);
        if (id == 0) {
            throw new JGemsRuntimeException("Could not create Shader: " + type);
        }
        GL46.glShaderSource(id, shader);
        GL46.glCompileShader(id);
        if (GL46.glGetShaderi(id, GL46.GL_COMPILE_STATUS) == 0) {
            Log.get().warn(shader);
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
        if (this.computeShaderId != 0) {
            GL46.glDetachShader(this.programId, this.computeShaderId);
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
