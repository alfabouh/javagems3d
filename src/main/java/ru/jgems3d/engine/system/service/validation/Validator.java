package ru.jgems3d.engine.system.service.validation;

import ru.jgems3d.engine.JGemsHelper;
import ru.jgems3d.engine.system.service.exceptions.JGemsValidationException;

public abstract class Validator {
    public static boolean validate(boolean check, String description, OnFail actionOnFailure) {
        if (check) {
            return true;
        } else {
            switch (actionOnFailure) {
                case LOG: {
                    JGemsHelper.getLogger().log(description);
                    return false;
                }
                case DEBUG: {
                    JGemsHelper.getLogger().debug(description);
                    return false;
                }
                case WARN: {
                    JGemsHelper.getLogger().warn(description);
                    return false;
                }
                case ERROR: {
                    JGemsHelper.getLogger().error(description);
                    return false;
                }
                case EXCEPTION: {
                    throw new JGemsValidationException(description);
                }
            }
        }
    }
}
