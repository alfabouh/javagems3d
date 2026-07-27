/*
 *
 *  * javagems3d
 *  * Copyright (C) 2026 gltexture
 *  *
 *  * This program is free software: you can redistribute it and/or modify
 *  * it under the terms of the GNU General Public License as published by
 *  * the Free Software Foundation, either version 3 of the License, or
 *  * (at your option) any later version.
 *  *
 *  * This program is distributed in the hope that it will be useful,
 *  * but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 *  * GNU General Public License for more details.
 *  *
 *  * You should have received a copy of the GNU General Public License
 *  * along with this program. If not, see <https://www.gnu.org/licenses/>.
 *
 */

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