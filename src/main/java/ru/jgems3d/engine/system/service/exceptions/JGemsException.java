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

package ru.jgems3d.engine.system.service.exceptions;

public abstract class JGemsException extends RuntimeException {
    public JGemsException() {
    }

    public JGemsException(String ex) {
        super(ex);
    }

    public JGemsException(String ex, Exception e) {
        super(ex, e);
    }

    public JGemsException(Exception ex) {
        super(ex);
    }
}
