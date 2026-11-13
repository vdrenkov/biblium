package dev.vdrenkov.biblium.exception;

import java.io.Serial;

public class OrderNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = -2524554727744243607L;
    private static final String ORDER_NOT_FOUND_MESSAGE = "No such order found in the database";

    @Override
    public String getMessage() {
        return ORDER_NOT_FOUND_MESSAGE;
    }
}
