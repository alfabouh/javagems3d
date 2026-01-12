package workbench.resources.frame;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;

public class LoadingInterfaceSwing {
    private static String resourceName = "";
    private static JFrame loadingFrame;
    private static JLabel loadingLabel;

    public static void setResource(String name) {
        SwingUtilities.invokeLater(() -> {
            LoadingInterfaceSwing.resourceName = name;
            LoadingInterfaceSwing.loadingLabel.setText("<html>" + LoadingInterfaceSwing.resourceName + "</html>");
        });
    }

    private static void create() {
        LoadingInterfaceSwing.loadingFrame = new JFrame("Loading...");
        LoadingInterfaceSwing.loadingFrame.setSize(500, 150);
        LoadingInterfaceSwing.loadingFrame.setLocationRelativeTo(null);
        LoadingInterfaceSwing.loadingFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        LoadingInterfaceSwing.loadingFrame.setResizable(false);

        LoadingInterfaceSwing.loadingLabel = new JLabel("Loading: ", SwingConstants.LEFT);
        LoadingInterfaceSwing.loadingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        LoadingInterfaceSwing.loadingFrame.add(loadingLabel, BorderLayout.CENTER);

        LoadingInterfaceSwing.loadingFrame.setVisible(true);
    }

    private static void close() {
        if (LoadingInterfaceSwing.loadingFrame != null) {
            LoadingInterfaceSwing.loadingFrame.dispose();
            LoadingInterfaceSwing.loadingFrame = null;
        }
    }

    public static void dispose() {
        SwingUtilities.invokeLater(LoadingInterfaceSwing::close);
    }

    public static void invoke() {
        SwingUtilities.invokeLater(LoadingInterfaceSwing::create);
    }

    public static boolean valid() {
        return LoadingInterfaceSwing.loadingFrame != null;
    }
}