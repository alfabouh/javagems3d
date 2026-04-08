package javagems3d.graphics.screen.window;

import com.google.common.io.ByteStreams;
import javagems3d.system.global.JGemsConfig;
import javagems3d.system.service.files.source.ISource;
import javagems3d.system.service.files.source.JGemsPathSource;
import logger.Log;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector2f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWVidMode;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import javagems3d.JGems3D;
import javagems3d.system.service.exceptions.JGemsNullException;
import javagems3d.system.service.files.JGemsPath;

import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class Window implements IWindow {
    public static final String DEFAULT_ICON = "/assets/icons/icon.png";
    
    private final long window;
    private long currentMonitor;
    private boolean isInFocus;

    private final long arrowCursor;
    private final long textCursor;

    public Window(int width, int height, WindowProperties windowProperties) {
        this.isInFocus = false;
        this.window = GLFW.glfwCreateWindow(width, height, windowProperties.title(), MemoryUtil.NULL, MemoryUtil.NULL);
        this.currentMonitor = GLFW.glfwGetPrimaryMonitor();
        this.setIcon(windowProperties.icon(), ISource.Source.INSIDE_JAR);

        {
            this.arrowCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_ARROW_CURSOR);
            this.textCursor = GLFW.glfwCreateStandardCursor(GLFW.GLFW_IBEAM_CURSOR);
        }
    }

    public void setTextCursor() {
        GLFW.glfwSetCursor(this.getDescriptor(), this.textCursor);
    }

    public void setArrowCursor() {
        GLFW.glfwSetCursor(this.getDescriptor(), this.arrowCursor);
    }

    @Override
    public void setTitle(@NotNull String title) {
        GLFW.glfwSetWindowTitle(this.getDescriptor(), title);
        Log.get().info("Changed title -> " + title);
    }
    
    @Override
    public void setIcon(@Nullable JGemsPath iconPath, ISource.Source source) {
        if (iconPath == null) {
            Log.get().warn("Couldn't load app icon, because it was NULL");
            return;
        }
        try (MemoryStack stack = MemoryStack.stackPush()) {
            try (InputStream inputStream = JGems3D.getInputStream(new JGemsPathSource(iconPath, source))) {
                IntBuffer width = stack.mallocInt(1);
                IntBuffer height = stack.mallocInt(1);
                IntBuffer channels = stack.mallocInt(1);
                byte[] stream = ByteStreams.toByteArray(inputStream);
                ByteBuffer buffer = MemoryUtil.memAlloc(stream.length);
                buffer.put(stream);
                buffer.flip();
                ByteBuffer imageBuffer = STBImage.stbi_load_from_memory(buffer, width, height, channels, STBImage.STBI_rgb_alpha);
                if (imageBuffer == null) {
                    throw new NullPointerException("WINDOW icon is NULL");
                }
                GLFWImage.Buffer iconBuffer = GLFWImage.malloc(1);
                iconBuffer.width(width.get(0));
                iconBuffer.height(height.get(0));
                iconBuffer.pixels(imageBuffer);
                GLFW.glfwSetWindowIcon(window, iconBuffer);
                STBImage.stbi_image_free(imageBuffer);
            }
        } catch (Exception e) {
            Log.get().exception(e);
        }
        Log.get().info("Installed icon: " + iconPath);
    }

    public void onWindowChangedCallback() {
        IntBuffer xPos = BufferUtils.createIntBuffer(1);
        IntBuffer yPos = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetWindowPos(window, xPos, yPos);

        IntBuffer width = BufferUtils.createIntBuffer(1);
        IntBuffer height = BufferUtils.createIntBuffer(1);
        GLFW.glfwGetWindowSize(window, width, height);

        int centerX = xPos.get(0) + width.get(0) / 2;
        int centerY = yPos.get(0) + height.get(0) / 2;

        this.currentMonitor = GLFW.glfwGetPrimaryMonitor();
        PointerBuffer pointerBuffer = GLFW.glfwGetMonitors();
        if (pointerBuffer != null) {
            for (int i = 0; i < pointerBuffer.capacity(); i++) {
                long m = pointerBuffer.get(i);
                GLFWVidMode vidMode = GLFW.glfwGetVideoMode(m);
                if (vidMode == null) {
                    continue;
                }
                IntBuffer mxPos = BufferUtils.createIntBuffer(1);
                IntBuffer myPos = BufferUtils.createIntBuffer(1);
                GLFW.glfwGetMonitorPos(m, mxPos, myPos);
                if (mxPos.get(0) <= centerX && centerX < mxPos.get(0) + vidMode.width() && myPos.get(0) <= centerY && centerY < myPos.get(0) + vidMode.height()) {
                    this.currentMonitor = m;
                    break;
                }
            }
        }
    }

    public void hideWindow() {
        GLFW.glfwHideWindow(this.getDescriptor());
    }

    public void showWindow() {
        GLFW.glfwShowWindow(this.getDescriptor());
        GLFW.glfwFocusWindow(this.getDescriptor());
    }

    public boolean isWindowInFocus() {
        return this.isWindowActive() && this.isInFocus;
    }

    public void setFocus(boolean inFocus) {
        isInFocus = inFocus;
    }

    public void switchFocus() {
        this.isInFocus = !this.isInFocus;
    }

    public void setCursorFocused(boolean focus) {
        GLFW.glfwSetInputMode(this.getDescriptor(), GLFW.GLFW_CURSOR, focus ? GLFW.GLFW_CURSOR_DISABLED : GLFW.GLFW_CURSOR_NORMAL);
    }

    public int monitorRefreshRate() {
        GLFWVidMode glfwVidMode = GLFW.glfwGetVideoMode(this.getCurrentMonitor());
        assert glfwVidMode != null;
        return glfwVidMode.refreshRate();
    }

    public int getPosX() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            GLFW.glfwGetWindowPos(this.window, x, null);
            return x.get(0);
        }
    }

    public int getPosY() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer y = stack.mallocInt(1);
            GLFW.glfwGetWindowPos(this.window, null, y);
            return y.get(0);
        }
    }

    public Vector4f getWindowFrameSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer l = stack.mallocInt(1);
            IntBuffer t = stack.mallocInt(1);
            IntBuffer r = stack.mallocInt(1);
            IntBuffer b = stack.mallocInt(1);
            GLFW.glfwGetWindowFrameSize(this.window, l, t, r, b);
            return new Vector4f(l.get(0), t.get(0), r.get(0), b.get(0));
        }
    }

    public Vector2f getWindowPos() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer x = stack.mallocInt(1);
            IntBuffer y = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.window, x, y);
            return new Vector2f(x.get(0), y.get(0));
        }
    }

    public Vector2i getWindowSize() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer width = stack.mallocInt(1);
            IntBuffer height = stack.mallocInt(1);
            GLFW.glfwGetWindowSize(this.window, width, height);
            return new Vector2i(width.get(0), height.get(0));
        }
    }

    public void enableVSync() {
        GLFW.glfwSwapInterval(1);
    }

    public void disableVSync() {
        GLFW.glfwSwapInterval(0);
    }

    public void makeFullScreen() {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode == null) {
            throw new JGemsNullException("Null Monitor");
        }
        GLFW.glfwSetWindowMonitor(this.getDescriptor(), GLFW.glfwGetPrimaryMonitor(), 0, 0, vidMode.width(), vidMode.height(), GLFW.GLFW_DONT_CARE);
        Log.get().trace("FullScreen mode");
    }

    public void removeFullScreen() {
        GLFWVidMode vidMode = GLFW.glfwGetVideoMode(GLFW.glfwGetPrimaryMonitor());
        if (vidMode == null) {
            throw new JGemsNullException("Null Monitor");
        }
        int x = (vidMode.width() - JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH) / 2;
        int y = (vidMode.height() - JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT) / 2;
        GLFW.glfwSetWindowMonitor(this.getDescriptor(), 0, x, y, JGemsConfig.SYSTEM.DEFAULT_SCREEN_WIDTH, JGemsConfig.SYSTEM.DEFAULT_SCREEN_HEIGHT, GLFW.GLFW_DONT_CARE);
        Log.get().trace("DefaultScreen mode");
    }

    public long getCurrentMonitor() {
        return this.currentMonitor;
    }

    public long getDescriptor() {
        return this.window;
    }

    public record WindowProperties(String title, JGemsPath icon) {
            public WindowProperties(@NotNull String title) {
                this(title, new JGemsPath(Window.DEFAULT_ICON));
            }

            public WindowProperties(@NotNull String title, @Nullable JGemsPath icon) {
                this.title = title;
                this.icon = icon;
            }
        }
}
