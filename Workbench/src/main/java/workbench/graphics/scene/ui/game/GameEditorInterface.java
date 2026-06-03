package workbench.graphics.scene.ui.game;

import api.system.JGemsAPI;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import javagems3d.JGems3D;
import javagems3d.graphics.rendering.programs.fbo.FBOTexture2DProgram;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIGameInterface;
import javagems3d.graphics.rendering.ui.dear_imgui.interfaces.DearUIInterface;
import javagems3d.help.JGemsHelper;
import javagems3d.system.controller.base.MouseKeyboardController;
import javagems3d.system.core.JGemsLaunchArgsRegistry;
import javagems3d.system.external.gaming.JGemsGameInstance;
import javagems3d.system.service.collections.Pair;
import javagems3d.system.service.exceptions.JGemsIOException;
import logger.Log;
import logger.managers.LoggingManager;
import org.joml.Vector2i;
import workbench.WBench;
import workbench.graphics.scene.renderer.WBenchOpenGLRenderer;
import workbench.graphics.scene.ui.ProjectUIUtils;
import workbench.graphics.scene.ui.game.editor.ActionsInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.ResourcesInterfaceComponentG;
import workbench.graphics.scene.ui.game.editor.WindowInterfaceComponentG;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.Set;

public class GameEditorInterface implements DearUIInterface {
    public static boolean isCursorInsideScene;
    private final WBenchOpenGLRenderer openGLRenderer;
    private final ResourcesInterfaceComponentG resourcesInterfaceComponentG;
    private final ActionsInterfaceComponentG actionsInterfaceComponentG;
    private final WindowInterfaceComponentG windowInterfaceComponentG;

    public GameEditorInterface(WBenchOpenGLRenderer openGLRenderer, FBOTexture2DProgram scenePreview) {
        this.openGLRenderer = openGLRenderer;
        this.resourcesInterfaceComponentG = new ResourcesInterfaceComponentG(this);
        this.actionsInterfaceComponentG = new ActionsInterfaceComponentG(this.resourcesInterfaceComponentG);
        this.windowInterfaceComponentG = new WindowInterfaceComponentG(this.getOpenGLRenderer(), this.actionsInterfaceComponentG, this.resourcesInterfaceComponentG, scenePreview);
        this.clear();
    }

    public void clear() {
        this.resourcesInterfaceComponentG.clear();
        //this.actionsInterfaceComponentG.clear();
        //this.windowInterfaceComponentG.clear();
    }

    @Override
    public void drawGui(Vector2i windowSize, MouseKeyboardController mouseKeyboardController) {
        if (false) {
            ImGui.setNextWindowFocus();
            ImGui.showDemoWindow();
        }

        if (ProjectUIUtils.ctrlS()) {
            WBench.get().getGameProjectManager().saveGameProject(false);
            Log.get().info("Saved...");
        }

        final float YOffset = ImGui.getFrameHeight();

        final float sceneWindowSizeX = windowSize.x * 0.5f;
        final float sceneWindowSizeY = windowSize.y * 0.7f;

        final float consoleWindowSizeX = sceneWindowSizeX;
        final float consoleWindowSizeY = windowSize.y - sceneWindowSizeY;
        final float sceneWindowOffset = (windowSize.x - sceneWindowSizeX) * 0.5f;

        final float resourcesWindowSizeX = (windowSize.x - sceneWindowSizeX) - sceneWindowOffset;
        final float resourcesWindowSizeY =  windowSize.y * 1.0f;

        final float propertiesWindowSizeX = (windowSize.x - sceneWindowSizeX) - sceneWindowOffset;
        final float propertiesWindowSizeY =  windowSize.y;

        ImGui.beginMainMenuBar();
        if (ImGui.beginMenu("Game Project")) {
            //TODO
            if (ImGui.menuItem("Run Game")) {
                //JGems3D.IsolatedProcessLauncher.EXEC(JGemsLaunchArgsRegistry.getArgumentFrom(
                //        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.MAP_TEST, "true"),
                //        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.DEBUG, "true"),
                //        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.NO_SOUND, "true"),
                //        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.NO_FULL_SCREEN, "true")
                //));
                WBench.get().getGameProjectManager().saveGameProject(true);
                JGems3D.IsolatedProcessLauncher.EXEC(JGemsLaunchArgsRegistry.getArgumentFrom(
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.DEBUG, "true"),
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.NO_SOUND, "false"),
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.EXTERNAL_GAME_DEF, WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath().fullPath()),
                        new Pair<>(JGemsLaunchArgsRegistry.DEFAULT_ARGS.API_APP_CLASSPATH, JGemsAPI.getExternalClassApiDef())
                ));
            }
            ImGui.separator();
            if (ImGui.menuItem("Save")) {
                Log.get().info("Saved...");
                WBench.get().getGameProjectManager().saveGameProject(false);
            }
            //TODO
            if (ImGui.menuItem("Exit")) {
                if (LoggingManager.showConfirmationWindowDialog("Are you sure?")) {
                    WBench.get().getGameProjectManager().closeGameProject();
                    ImGui.endMenu();
                    ImGui.endMainMenuBar();
                    return;
                }
            }
            ImGui.endMenu();
        }
        if (ImGui.beginMenu("Build")) {
            ImGui.beginDisabled(JGems3D.isIDEA());
            if (ImGui.menuItem("Compile")) {
                String pathToSafe = JGemsHelper.files().openFolderViewChooser("");
                if (pathToSafe != null && !pathToSafe.isEmpty()) {
                    boolean err = false;
                    if (!new File(WBench.get().getGameProjectManager().gameProjectSettings.compileCorePath).exists()) {
                        LoggingManager.showWindowWarn("Core " + WBench.get().getGameProjectManager().gameProjectSettings.compileCorePath + " doesn't exist");
                        err = true;
                    }
                    if (!new File(WBench.get().getGameProjectManager().gameProjectSettings.compileLauncherPath).exists()) {
                        LoggingManager.showWindowWarn("Launcher " + WBench.get().getGameProjectManager().gameProjectSettings.compileLauncherPath + " doesn't exist");
                        err = true;
                    } if (!new File(WBench.get().getGameProjectManager().gameProjectSettings.compileWorkbenchPath).exists()) {
                        LoggingManager.showWindowWarn("Workbench " + WBench.get().getGameProjectManager().gameProjectSettings.compileWorkbenchPath + " doesn't exist");
                        err = true;
                    }
                    if (!err) {
                        File f = new File(pathToSafe, WBench.get().getGameProjectManager().getGameProject().getGameTitle());
                        if (f.exists() || f.mkdir()) {
                            this.copyRuntime(f);
                            File gameFiles = new File(f, "game_dir");
                            if (gameFiles.exists() || gameFiles.mkdir()) {
                                JGemsHelper.files().copyDirectory(WBench.get().getGameProjectManager().getGameProject().getProjectAbsolutePath().toFile(), gameFiles, JGemsGameInstance.TEMP_FILE);
                            } else {
                                throw new JGemsIOException("Unable to create project folder " + gameFiles.getPath());
                            }
                            this.createBatSh(f);
                            LoggingManager.showWindowInfo("Success!");
                        } else {
                            throw new JGemsIOException("Unable to create project folder " + f.getPath());
                        }
                    }
                }
            }
            ImGui.endDisabled();
            ImGui.endMenu();
        }
        ImGui.endMainMenuBar();

        ImGui.begin("Output",ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        final boolean flagOut = ImGui.isWindowCollapsed();
        ImGui.setWindowSize(consoleWindowSizeX, consoleWindowSizeY);
        ImGui.setWindowPos(sceneWindowOffset, flagOut ? windowSize.y - YOffset : windowSize.y - consoleWindowSizeY);
        DearUIGameInterface.consoleContent();
        ImGui.end();

        ImGui.begin("Window", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus | ImGuiWindowFlags.MenuBar);
        if (ImGui.isWindowHovered()) {
            if (ImGui.isMouseClicked(1)) {
                ImGui.setWindowFocus();
            }
            GameEditorInterface.isCursorInsideScene = true;
        } else if (!WBench.get().getControllerDispatcher().getCurrentController().getMouseAndKeyboard().isRightKeyPressed()) {
            GameEditorInterface.isCursorInsideScene = false;
        }

        ImGui.sameLine();
        int posX = (int) sceneWindowOffset;
        int posY = (int) YOffset;
        int sizeX = (int) sceneWindowSizeX;
        int sizeY = (int) (flagOut ? (windowSize.y - YOffset * 2) : sceneWindowSizeY - YOffset);
        ImGui.setWindowSize(sizeX, sizeY);
        ImGui.setWindowPos(posX, posY);
        this.getWindowInterfaceComponentG().windowContent();
        ImGui.end();

        ImGui.begin("Resources", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(resourcesWindowSizeX, resourcesWindowSizeY);
        ImGui.setWindowPos(0, posY);
        //this.getResourcesComponent().resourcesContent();
        this.getResourcesInterfaceComponentG().resourcesContent();
        ImGui.end();

        ImGui.begin("Actions", ImGuiWindowFlags.NoResize | ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoMove | ImGuiWindowFlags.NoBringToFrontOnFocus);
        ImGui.setWindowSize(propertiesWindowSizeX, propertiesWindowSizeY - YOffset);
        ImGui.setWindowPos(sceneWindowSizeX + sceneWindowOffset, YOffset);
        this.getActionsInterfaceComponentG().actionsContent();
        ImGui.end();

        //this.getContextComponent().context();
    }

    private void createBatSh(File dir) {
        this.createWorkbenchBat(dir);
        this.createCoreBat(dir);
        this.createWorkbenchSh(dir);
        this.createCoreSh(dir);
    }

    private void makeExecutable(File file) {
        try {
            Files.setPosixFilePermissions(file.toPath(),
                    Set.of(
                            PosixFilePermission.OWNER_EXECUTE,
                            PosixFilePermission.OWNER_READ,
                            PosixFilePermission.OWNER_WRITE,
                            PosixFilePermission.GROUP_EXECUTE,
                            PosixFilePermission.GROUP_READ,
                            PosixFilePermission.OTHERS_EXECUTE,
                            PosixFilePermission.OTHERS_READ
                    )
            );
        } catch (Exception ignored) {}
    }

    private void createCoreSh(File dir) {
        File sh = new File(dir, "jgems3d-core-run.sh");

        String content =
                "#!/bin/bash\n" +
                        "DIR=\"$(cd \"$(dirname \"$0\")\" && pwd)\"\n" +
                        "java -Dpolyglotimpl.DisableMultiReleaseCheck=true --enable-native-access=ALL-UNNAMED -XX:+UseG1GC -Xms512m -Xmx4G -cp \"$DIR/core/jgems3d-core.jar:$DIR/core/jgems3d-launcher.jar\" launcher.bootstrap.Bootstrap workbench=false external_def=\"$DIR/game_dir\"\n";

        this.writeFile(sh, content);
        this.makeExecutable(sh);
    }

    private void createWorkbenchSh(File dir) {
        File sh = new File(dir, "jgems3d-workbench-run.sh");

        String content =
                "#!/bin/bash\n" +
                        "DIR=\"$(cd \"$(dirname \"$0\")\" && pwd)\"\n" +
                        "java -Dpolyglotimpl.DisableMultiReleaseCheck=true --enable-native-access=ALL-UNNAMED -XX:+UseG1GC -Xms512m -Xmx4G -cp \"$DIR/core/jgems3d-core.jar:$DIR/core/jgems3d-launcher.jar:$DIR/core/jgems3d-workbench.jar\" launcher.bootstrap.Bootstrap workbench=true\n";

        this.writeFile(sh, content);
        this.makeExecutable(sh);
    }

    private void createCoreBat(File dir) {
        File bat = new File(dir, "jgems3d-core-run.bat");

        String content =
                "@echo off\n" +
                        "java -Dpolyglotimpl.DisableMultiReleaseCheck=true --enable-native-access=ALL-UNNAMED -XX:+UseG1GC -Xms512m -Xmx4G -cp \"./core/jgems3d-core.jar;./core/jgems3d-launcher.jar\" launcher.bootstrap.Bootstrap workbench=false external_def=\"./game_dir\"\n" +
                        "pause\n";

        this.writeFile(bat, content);
    }

    private void createWorkbenchBat(File dir) {
        File bat = new File(dir, "jgems3d-workbench-run.bat");

        String content =
                "@echo off\n" +
                        "java -Dpolyglotimpl.DisableMultiReleaseCheck=true --enable-native-access=ALL-UNNAMED -XX:+UseG1GC -Xms512m -Xmx4G -cp \"./core/jgems3d-core.jar;./core/jgems3d-launcher.jar;./core/jgems3d-workbench.jar\" launcher.bootstrap.Bootstrap workbench=true\n" +
                        "pause\n";

        this.writeFile(bat, content);
    }

    private void writeFile(File file, String content) {
        try {
            Files.writeString(file.toPath(), content, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new RuntimeException("Failed to write " + file.getPath(), e);
        }
    }

    private File getRunDir() {
        try {
            return new File(WBench.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (Exception e) {
            throw new JGemsIOException(e);
        }
    }

    public void copyRuntime(File targetDir) {
        File runDir = this.getRunDir();
        new File(targetDir, "core").mkdir();
        JGemsHelper.files().copyFile(new File(WBench.get().getGameProjectManager().gameProjectSettings.compileCorePath), new File(targetDir, "./core/jgems3d-core.jar"));
        JGemsHelper.files().copyFile(new File(WBench.get().getGameProjectManager().gameProjectSettings.compileLauncherPath), new File(targetDir, "./core/jgems3d-launcher.jar"));
        JGemsHelper.files().copyFile(new File(WBench.get().getGameProjectManager().gameProjectSettings.compileWorkbenchPath), new File(targetDir, "./core/jgems3d-workbench.jar"));
        JGemsHelper.files().copyDirectory(new File(runDir, "api"), new File(targetDir, "./core/api"), null);
    }

    public WindowInterfaceComponentG getWindowInterfaceComponentG() {
        return this.windowInterfaceComponentG;
    }

    public ActionsInterfaceComponentG getActionsInterfaceComponentG() {
        return this.actionsInterfaceComponentG;
    }

    public ResourcesInterfaceComponentG getResourcesInterfaceComponentG() {
        return this.resourcesInterfaceComponentG;
    }

    public WBenchOpenGLRenderer getOpenGLRenderer() {
        return this.openGLRenderer;
    }
}