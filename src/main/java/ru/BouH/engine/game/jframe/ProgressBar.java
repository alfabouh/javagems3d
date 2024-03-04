package ru.BouH.engine.game.jframe;

import ru.BouH.engine.math.MathHelper;

import javax.swing.*;

public class ProgressBar {
    private final JFrame frame;
    private final JProgressBar jProgressBar;

    public ProgressBar() {
        this.frame = new JFrame("Launching");
        this.jProgressBar = new JProgressBar(0, 100);
    }

    public void setProgress(int a) {
        this.jProgressBar.setValue(MathHelper.clamp(a, 0, 100));
    }

    public void showBar() {
        this.frame.add(this.jProgressBar);
        this.frame.setSize(600, 60);
        this.frame.setResizable(false);
        this.frame.setDefaultCloseOperation(WindowConstants.DO_NOTHING_ON_CLOSE);
        this.frame.setLocationRelativeTo(null);
        this.frame.setVisible(true);
    }

    public void hideBar() {
        this.frame.dispose();
    }
}
