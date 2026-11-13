package dev.vdrenkov.biblium.exception;

import java.io.Serial;

public class ClientNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 7011694164583624439L;
    private static final String CLIENT_NOT_FOUND_MESSAGE = "No such client found in the database";

    @Override
    public String getMessage() {
        return CLIENT_NOT_FOUND_MESSAGE;
    }
}
