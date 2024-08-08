package ru.jgems3d.engine.system.service.validation;

import jme3utilities.Validate;
import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.exceptions.JGemsException;
import ru.jgems3d.engine.system.service.exceptions.JGemsNullObjectException;
import ru.jgems3d.engine.system.service.exceptions.JGemsPathNotFoundException;
import ru.jgems3d.engine.system.service.exceptions.JGemsValidationException;
import ru.jgems3d.engine.system.service.misc.JGPath;

import java.io.InputStream;

public abstract class Validator {
    public static boolean pathExists(InputStream inputStream, JGPath path, OnFail onFail) {
        if (inputStream != null) {
            return true;
        } else {
            String desc = "Path " + path + " not found!";
            switch (onFail) {
                case CONSOLE: {
                    JGemsHelper.getLogger().error(desc);
                    break;
                }
                case EXCEPTION: {
                    throw new JGemsPathNotFoundException(desc);
                }
            }
        }
        return false;
    }

    public static boolean notNull(Object o, String objectName, OnFail onFail) {
        if (o != null) {
            return true;
        } else {
            String desc = objectName + " - IS NULL!";
            switch (onFail) {
                case CONSOLE: {
                    JGemsHelper.getLogger().error(desc);
                    break;
                }
                case EXCEPTION: {
                    throw new JGemsNullObjectException(desc);
                }
            }
        }
        return false;
    }

    public static boolean validate(boolean check, String what, OnFail onFail) {
        if (check) {
            return true;
        } else {
            String desc = "Validation failed: " + what;
            switch (onFail) {
                case CONSOLE: {
                    JGemsHelper.getLogger().error(desc);
                    break;
                }
                case EXCEPTION: {
                    throw new JGemsValidationException(desc);
                }
            }
        }
        return false;
    }
}
