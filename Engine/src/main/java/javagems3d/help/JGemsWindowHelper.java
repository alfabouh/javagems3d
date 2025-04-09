package javagems3d.help;

public abstract class JGemsWindowHelper {
    public static void setWindowFocus(boolean focus) {
        JGemsCoreHelper.getScreen().getWindow().setFocus(focus);
    }

    public static boolean isWindowActive() {
        return JGemsCoreHelper.getScreen().getWindow().isWindowActive();
    }
}
