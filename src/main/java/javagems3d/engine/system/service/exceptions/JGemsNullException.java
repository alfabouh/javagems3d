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

public class JGemsNullException extends JGemsException {
    public JGemsNullException() {
    }

    public JGemsNullException(String ex) {
        super(ex);
    }

    public JGemsNullException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsNullException(Exception ex) {
        super(ex);
    }
}
