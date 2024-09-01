package engine_api;

import javagems3d.engine.JGems3D;
import toolbox.ToolBox;

import javax.tools.Tool;

public abstract class Launcher {
    public static void launchEngine(String[] args) {
        JGems3D.launch(args);
    }

    public static void launchToolBox(String[] args) {
        ToolBox.launch(args);
    }
}
