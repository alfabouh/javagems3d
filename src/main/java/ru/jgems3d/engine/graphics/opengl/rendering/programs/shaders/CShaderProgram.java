package ru.jgems3d.engine.graphics.opengl.rendering.programs.shaders;

import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL43;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.exceptions.JGemsException;
import ru.jgems3d.engine.system.resources.assets.shaders.Shader;

public class CShaderProgram implements IShaderProgram {
    private final int programId;
    private int computeShaderId;

    public CShaderProgram() {
        this.programId = GL20.glCreateProgram();
        if (this.programId == 0) {
            throw new JGemsException("Could not create shader program!");
        }
    }

    public boolean createShader(Shader compShader) {
        if (compShader == null) {
            return false;
        }
        this.createComputeShader(compShader.getShaderText());
        return true;
    }

    public void createComputeShader(String shader) {
        this.computeShaderId = this.createShader(shader, GL43.GL_COMPUTE_SHADER);
    }

    private int createShader(String shader, int type) {
        int id = GL20.glCreateShader(type);
        if (id == 0) {
            throw new JGemsException("Could not create Shader: " + type);
        }
        GL20.glShaderSource(id, shader);
        GL20.glCompileShader(id);
        if (GL20.glGetShaderi(id, GL20.GL_COMPILE_STATUS) == 0) {
            JGemsHelper.getLogger().warn(shader);
            throw new JGemsException("Compile shader error: " + GL20.glGetShaderInfoLog(id, 4096));
        }
        GL20.glAttachShader(this.programId, id);
        return id;
    }

    public boolean link() {
        GL20.glLinkProgram(this.programId);
        if (GL20.glGetProgrami(this.programId, GL20.GL_LINK_STATUS) == 0) {
            String err = GL20.glGetShaderInfoLog(this.programId, 4096);
            if (err.isEmpty()) {
                err = "UNKNOWN ERR";
            }
            JGemsHelper.getLogger().warn("Could not link shader: " + err);
            //return false;
        }
        if (this.computeShaderId != 0) {
            GL20.glDetachShader(this.programId, this.computeShaderId);
        }
        GL20.glValidateProgram(this.programId);
        if (GL20.glGetProgrami(this.programId, GL20.GL_VALIDATE_STATUS) == 0) {
            String err = GL20.glGetShaderInfoLog(this.programId, 4096);
            if (!err.isEmpty()) {
                JGemsHelper.getLogger().warn("Could not validate shader " + err);
                return false;
            }
        }
        return true;
    }

    public int getProgramId() {
        return this.programId;
    }
}
