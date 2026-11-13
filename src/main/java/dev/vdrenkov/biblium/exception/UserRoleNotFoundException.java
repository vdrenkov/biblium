package dev.vdrenkov.biblium.exception;

import java.io.Serial;

public class UserRoleNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -5184861651681529567L;
    private static final String ROLE_NOT_FOUND_MESSAGE = "No such role found in the database";

    @Override
    public String getMessage() {
        return ROLE_NOT_FOUND_MESSAGE;
    }
}
