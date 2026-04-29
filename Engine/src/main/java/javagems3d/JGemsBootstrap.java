package javagems3d;

import javagems3d.system.core.JGemsLaunchArgsRegistry;

final class JGemsBootstrap {
    public static void main(String[] args) {
        JGemsLaunchArgsRegistry.INSTANCE.read(args);
        JGems3D.launch(JGemsLaunchArgsRegistry.INSTANCE);
    }
}