/*
 * *
 *  * @author alfabouh
 *  * @since 2024
 *  * @link https://github.com/alfabouh/JavaGems3D
 *  *
 *  * This software is provided 'as-is', without any express or implied warranty.
 *  * In no event will the authors be held liable for any damages arising from the use of this software.
 *
 */

package javagems3d.engine.system.service.exceptions;

public class JGemsNotFoundException extends JGemsException {
    public JGemsNotFoundException() {
    }

    public JGemsNotFoundException(String ex) {
        super(ex);
    }

    public JGemsNotFoundException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsNotFoundException(Exception ex) {
        super(ex);
    }
}
