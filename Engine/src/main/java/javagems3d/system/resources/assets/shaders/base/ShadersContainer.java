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

package javagems3d.system.resources.assets.shaders.base;

import javagems3d.system.resources.assets.shaders.constants.ShaderStaticConstants;
import javagems3d.system.resources.assets.shaders.libraries.ShaderLibrariesManager;
import javagems3d.system.resources.assets.shaders.uniform.Uniform;
import javagems3d.system.service.path.JGemsPath;
import logger.Log;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class ShadersContainer {
    private final Set<Uniform> gUniformsFullSet;
    private final Set<Uniform> cUniformsFullSet;

    private final ShaderObject vertexShaderObject;
    private final ShaderObject fragmentShaderObject;
    private final ShaderObject geometricShaderObject;
    private final ShaderObject tesselationControlShaderObject;
    private final ShaderObject tesselationEvaluationShaderObject;

    private final ShaderObject computeShaderObject;
    private final String id;

    public ShadersContainer(JGemsPath shaderPath) {
        this(null, null, shaderPath);
    }

    public ShadersContainer(ShaderStaticConstants shaderStaticConstants, ShaderLibrariesManager shaderLibrary, JGemsPath shaderPath) {
        this.id = shaderPath.getFullPath();
        this.gUniformsFullSet = new HashSet<>();
        this.cUniformsFullSet = new HashSet<>();
        ShaderObject geometricShaderObject1 = null;
        ShaderObject vertexShaderObject1 = null;
        ShaderObject fragmentShaderObject1 = null;
        ShaderObject computeShaderObject1 = null;
        ShaderObject tesselationControlShaderObject1 = null;
        ShaderObject tesselationEvaluationShaderObject1 = null;

        if (ShaderObject.checkIfShaderExistsInJar(shaderPath, ShaderType.FRAGMENT)) {
            fragmentShaderObject1 = new ShaderObject(shaderStaticConstants,shaderLibrary, ShaderType.FRAGMENT, shaderPath);
        }
        if (ShaderObject.checkIfShaderExistsInJar(shaderPath, ShaderType.VERTEX)) {
            vertexShaderObject1 = new ShaderObject(shaderStaticConstants,shaderLibrary, ShaderType.VERTEX, shaderPath);
        }
        if (ShaderObject.checkIfShaderExistsInJar(shaderPath, ShaderType.GEOMETRIC)) {
            geometricShaderObject1 = new ShaderObject(shaderStaticConstants,shaderLibrary, ShaderType.GEOMETRIC, shaderPath);
        }
        if (ShaderObject.checkIfShaderExistsInJar(shaderPath, ShaderType.TESS_CONTROL)) {
            tesselationControlShaderObject1 = new ShaderObject(shaderStaticConstants,shaderLibrary, ShaderType.TESS_CONTROL, shaderPath);
        }
        if (ShaderObject.checkIfShaderExistsInJar(shaderPath, ShaderType.TESS_EVALUATION)) {
            tesselationEvaluationShaderObject1 = new ShaderObject(shaderStaticConstants,shaderLibrary, ShaderType.TESS_EVALUATION, shaderPath);
        }

        if (ShaderObject.checkIfShaderExistsInJar(shaderPath, ShaderType.COMPUTE)) {
            computeShaderObject1 = new ShaderObject(shaderStaticConstants,shaderLibrary, ShaderType.COMPUTE, shaderPath);
        }

        this.vertexShaderObject = vertexShaderObject1;
        this.tesselationControlShaderObject = tesselationControlShaderObject1;
        this.tesselationEvaluationShaderObject = tesselationEvaluationShaderObject1;
        this.fragmentShaderObject = fragmentShaderObject1;
        this.geometricShaderObject = geometricShaderObject1;

        this.computeShaderObject = computeShaderObject1;
    }

    public void initAll() {
        if (this.getFragmentShader() != null) {
            Log.get().trace("Initializing " + this.getFragmentShader().getShaderPath() + "/" + this.getFragmentShader().getShaderType().getFile());
            this.getFragmentShader().init();
            this.putUniformsInGHeap(this.getFragmentShader().getUniforms());
        }
        if (this.getVertexShader() != null) {
            Log.get().trace("Initializing " + this.getVertexShader().getShaderPath() + "/" + this.getVertexShader().getShaderType().getFile());
            this.getVertexShader().init();
            this.putUniformsInGHeap(this.getVertexShader().getUniforms());
        }
        if (this.getGeometricShader() != null) {
            Log.get().trace("Initializing " + this.getGeometricShader().getShaderPath() + "/" + this.getGeometricShader().getShaderType().getFile());
            this.getGeometricShader().init();
            this.putUniformsInGHeap(this.getGeometricShader().getUniforms());
        }
        if (this.getTesselationControlShader() != null) {
            Log.get().trace("Initializing " + this.getTesselationControlShader().getShaderPath() + "/" + this.getTesselationControlShader().getShaderType().getFile());
            this.getTesselationControlShader().init();
            this.putUniformsInGHeap(this.getTesselationControlShader().getUniforms());
        }
        if (this.getTesselationEvaluationShader() != null) {
            Log.get().trace("Initializing " + this.getTesselationEvaluationShader().getShaderPath() + "/" + this.getTesselationEvaluationShader().getShaderType().getFile());
            this.getTesselationEvaluationShader().init();
            this.putUniformsInGHeap(this.getTesselationEvaluationShader().getUniforms());
        }
        if (this.getComputeShader() != null) {
            Log.get().trace("Initializing " + this.getComputeShader().getShaderPath() + "/" + this.getComputeShader().getShaderType().getFile());
            this.getComputeShader().init();
            this.putUniformsInCHeap(this.getComputeShader().getUniforms());
        }
    }

    private void putUniformsInCHeap(List<Uniform> uniformList) {
        this.getCUniformsFullSet().addAll(uniformList);
    }

    private void putUniformsInGHeap(List<Uniform> uniformList) {
        this.getGUniformsFullSet().addAll(uniformList);
    }

    public void clear() {
        this.getGUniformsFullSet().clear();
        this.getCUniformsFullSet().clear();
        if (this.getFragmentShader() != null) {
            this.getFragmentShader().clear();
        }
        if (this.getVertexShader() != null) {
            this.getVertexShader().clear();
        }
        if (this.getGeometricShader() != null) {
            this.getGeometricShader().clear();
        }
        if (this.getTesselationControlShader() != null) {
            this.getTesselationControlShader().clear();
        }
        if (this.getTesselationEvaluationShader() != null) {
            this.getTesselationEvaluationShader().clear();
        }
        if (this.getComputeShader() != null) {
            this.getComputeShader().clear();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ShadersContainer that = (ShadersContainer) o;
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.id);
    }

    public Set<Uniform> getCUniformsFullSet() {
        return this.cUniformsFullSet;
    }

    public Set<Uniform> getGUniformsFullSet() {
        return this.gUniformsFullSet;
    }

    public String getId() {
        return this.id;
    }

    public ShaderObject getTesselationEvaluationShader() {
        return this.tesselationEvaluationShaderObject;
    }

    public ShaderObject getTesselationControlShader() {
        return this.tesselationControlShaderObject;
    }

    public ShaderObject getComputeShader() {
        return this.computeShaderObject;
    }

    public ShaderObject getFragmentShader() {
        return this.fragmentShaderObject;
    }

    public ShaderObject getGeometricShader() {
        return this.geometricShaderObject;
    }

    public ShaderObject getVertexShader() {
        return this.vertexShaderObject;
    }
}