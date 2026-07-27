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

package javagems3d.system.service.os;

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
