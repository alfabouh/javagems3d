package javagems3d.system.os;

import javagems3d.system.service.exceptions.JGemsRuntimeException;

public final class SysOSValidation {
    public static OS getCurrentOS() throws JGemsRuntimeException {
        String osName = System.getProperty("os.name").toLowerCase();
        String osArch = System.getProperty("os.arch").toLowerCase();

        if (osName.contains("win") && osArch.contains("amd64")) {
            return OS.Win64;
        } else if (osName.contains("linux") && osArch.contains("amd64")) {
            return OS.Lin64AMD;
        } else if (osName.contains("linux") && osArch.contains("arm64")) {
            return OS.Lin64ARM;
        } else {
            throw new JGemsRuntimeException("Unsupported OS: " + osName + " " + osArch);
        }
    }
}
