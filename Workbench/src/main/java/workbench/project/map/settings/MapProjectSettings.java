package workbench.project.map.settings;

public final class MapProjectSettings {
    public boolean VIEW_SHADOWS = true;
    public boolean VIEW_CHESS_TERRAIN = true;
    public boolean WIREFRAME_RENDERING = false;
    public boolean VIEW_HDR = true;
    public boolean ANIMATIONS = true;
    public boolean VIEW_FOG = true;
    public boolean FULL_BRIGHT = false;
    public float cameraX;
    public float cameraY;
    public float cameraZ;
    public float cameraRotX;
    public float cameraRotY;
    public float cameraRotZ;

    public MapProjectSettings() {
        this.cameraX = 0.0f;
        this.cameraY = 5.0f;
        this.cameraZ = 0.0f;
        this.cameraRotX = 0.0f;
        this.cameraRotY = 0.0f;
        this.cameraRotZ = 0.0f;
    }
}
