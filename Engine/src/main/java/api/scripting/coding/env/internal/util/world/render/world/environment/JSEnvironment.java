package api.scripting.coding.env.internal.util.world.render.world.environment;

import api.scripting.coding.env.def.JSCodingClass;
import api.scripting.coding.env.def.JSCodingConstructor;
import api.scripting.coding.env.def.JSCodingFunctionOrMethod;
import api.scripting.coding.env.def.JSHideFromDoc;
import api.scripting.coding.env.internal.util.math.JSVector3f;
import api.scripting.coding.env.internal.util.resources.instances.textures.JSTextureCubeMap;
import api.scripting.coding.env.internal.util.world.render.world.instances.interfaces.JSScenePropI;
import javagems3d.graphics.environment.JGemsEnvironment;
import javagems3d.graphics.environment.fog.FogScene;
import javagems3d.graphics.environment.lights.SunLight;
import javagems3d.graphics.environment.skybox.JGemsSkyBox;
import javagems3d.graphics.environment.skybox.background.JGemsSkyBackground;
import org.jetbrains.annotations.NotNull;

@JSCodingClass(binding = "JSEnvironment", description = "Provides access to rendering environment (skybox, fog, shadows, background).")
public class JSEnvironment {

    @JSHideFromDoc
    private final JGemsEnvironment environment;

    @JSCodingConstructor(description = "Wraps engine environment.", paramNames = {"environment"})
    public JSEnvironment(@NotNull JGemsEnvironment environment) {
        this.environment = environment;
    }

    @JSHideFromDoc
    @JSCodingFunctionOrMethod(description = "Returns the underlying Java environment.", paramNames = {})
    public JGemsEnvironment getJavaEnvironment() {
        return this.environment;
    }

    @JSHideFromDoc
    @JSCodingFunctionOrMethod(description = "Returns the underlying Java skybox.", paramNames = {})
    public JGemsSkyBox getJavaSkyBox() {
        return this.environment.getSkyBox();
    }

    @JSCodingFunctionOrMethod(description = "Returns background view scaling.", paramNames = {})
    public float getBackgroundViewScaling() {
        return this.getJavaSkyBackground().getViewScaling();
    }

    @JSCodingFunctionOrMethod(description = "Sets background view scaling.", paramNames = {"scaling"})
    public void setBackgroundViewScaling(float scaling) {
        this.getJavaSkyBackground().setViewScaling(scaling);
    }

    @JSCodingFunctionOrMethod(description = "Clears all background objects.", paramNames = {})
    public void clearBackground() {
        this.getJavaSkyBackground().clearBackGround();
    }

    @JSCodingFunctionOrMethod(description = "Returns number of objects in background.", paramNames = {})
    public int getBackgroundObjectCount() {
        return this.getJavaSkyBackground().getSkySceneObjects().size();
    }

    @JSCodingFunctionOrMethod(description = "Returns number of visible (culled) background objects.", paramNames = {})
    public int getVisibleBackgroundObjectCount() {
        return this.getJavaSkyBackground().getSkySceneObjectsFiltered().size();
    }

    @JSCodingFunctionOrMethod(description = "Returns background camera position.", paramNames = {})
    public JSVector3f getBackgroundCameraPosition() {
        return new JSVector3f(this.getJavaSkyBackground().getScaledCameraBackground().getCamPosition());
    }

    @JSCodingFunctionOrMethod(description = "Returns background camera rotation.", paramNames = {})
    public JSVector3f getBackgroundCameraRotation() {
        return new JSVector3f(this.getJavaSkyBackground().getScaledCameraBackground().getCamRotation());
    }

    @JSCodingFunctionOrMethod(description = "Adds object to sky background.", paramNames = {"prop"})
    public void addBackgroundObject(@NotNull JSScenePropI prop) {
        this.getJavaSkyBackground().addObject(prop.getJavaSceneProp());
    }

    @JSCodingFunctionOrMethod(description = "Removes object from sky background.", paramNames = {"prop"})
    public void removeBackgroundObject(@NotNull JSScenePropI prop) {
        this.getJavaSkyBackground().removeObject(prop.getJavaSceneProp());
    }

    @JSCodingFunctionOrMethod(description = "Returns fog density.", paramNames = {})
    public float getFogDensity() {
        return this.getJavaFogScene().getFogDensity();
    }

    @JSCodingFunctionOrMethod(description = "Sets fog density.", paramNames = {"density"})
    public void setFogDensity(float density) {
        this.getJavaFogScene().setFogDensity(density);
    }

    @JSCodingFunctionOrMethod(description = "Returns fog color.", paramNames = {})
    public JSVector3f getFogColor() {
        return new JSVector3f(this.getJavaFogScene().getFogColor());
    }

    @JSCodingFunctionOrMethod(description = "Sets fog color.", paramNames = {"color"})
    public void setFogColor(@NotNull JSVector3f color) {
        this.getJavaFogScene().setFogColor(color.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Disables fog (sets density to 0).", paramNames = {})
    public void disableFog() {
        this.getJavaFogScene().disable();
    }

    @JSCodingFunctionOrMethod(description = "Returns sun brightness.", paramNames = {})
    public float getSunBrightness() {
        return this.getJavaSun().getSunBrightness();
    }

    @JSCodingFunctionOrMethod(description = "Sets sun brightness.", paramNames = {"brightness"})
    public void setSunBrightness(float brightness) {
        this.getJavaSun().setSunBrightness(brightness);
    }

    @JSCodingFunctionOrMethod(description = "Returns sun position (direction).", paramNames = {})
    public JSVector3f getSunPosition() {
        return new JSVector3f(this.getJavaSun().getLightPosition());
    }

    @JSCodingFunctionOrMethod(description = "Sets sun position (direction).", paramNames = {"pos"})
    public void setSunPosition(@NotNull JSVector3f pos) {
        this.getJavaSun().setLightPosition(pos.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Returns sun color.", paramNames = {})
    public JSVector3f getSunColor() {
        return new JSVector3f(this.getJavaSun().getLightColor());
    }

    @JSCodingFunctionOrMethod(description = "Sets sun color.", paramNames = {"color"})
    public void setSunColor(@NotNull JSVector3f color) {
        this.getJavaSun().setLightColor(color.getJavaVector3f());
    }

    @JSCodingFunctionOrMethod(description = "Returns true if sky is affected by fog.", paramNames = {})
    public boolean isSkyCoveredByFog() {
        return this.getJavaSkyBox().isSkyCoveredByFog();
    }

    @JSCodingFunctionOrMethod(description = "Enable or disable fog influence on sky.", paramNames = {"value"})
    public void setSkyCoveredByFog(boolean value) {
        this.getJavaSkyBox().setSkyCoveredByFog(value);
    }

    @JSCodingFunctionOrMethod(description = "Returns true if sun is rendered on skybox.", paramNames = {})
    public boolean isDrawSun() {
        return this.getJavaSkyBox().isDrawSunOnSkyBox();
    }

    @JSCodingFunctionOrMethod(description = "Enable or disable sun rendering on skybox.", paramNames = {"value"})
    public void setDrawSun(boolean value) {
        this.getJavaSkyBox().setDrawSunOnSkyBox(value);
    }

    @JSCodingFunctionOrMethod(description = "Returns current sky background.", paramNames = {})
    public JGemsSkyBackground getJavaSkyBackground() {
        return (JGemsSkyBackground) this.getJavaSkyBox().getBackground();
    }

    @JSCodingFunctionOrMethod(description = "Returns fog scene instance.", paramNames = {})
    public FogScene getJavaFogScene() {
        return this.environment.getFogScene();
    }

    @JSCodingFunctionOrMethod(description = "Returns sun light instance.", paramNames = {})
    public SunLight getJavaSun() {
        return this.getJavaEnvironment().getLightScene().getSunLight();
    }

    @JSCodingFunctionOrMethod(description = "Returns skybox texture (cubemap).", paramNames = {})
    public JSTextureCubeMap getSkyTexture() {
        return new JSTextureCubeMap(this.getJavaSkyBox().getTexture());
    }

    @JSCodingFunctionOrMethod(description = "Sets skybox texture (cubemap).", paramNames = {"texture"})
    public void setSkyTexture(@NotNull JSTextureCubeMap texture) {
        this.getJavaSkyBox().setSky2DTexture(texture.getJavaCubeMapProgram());
    }
}