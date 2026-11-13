package dev.vdrenkov.biblium.util.constant;

import java.time.LocalDate;

public final class TestConstants {
    public static final int ID = 1;

    public static final int CHOICE = 3;

    public static final int PERIOD = 5;

    public static final int QUANTITY = 10;

    public static final int YEAR = 2000;

    public static final int MONTH = 1;

    public static final int DAY = 1;

    public static final String CHOICE_STRING = "3";

    public static final String PERIOD_STRING = "5";

    public static final LocalDate LOCAL_DATE = LocalDate.of(YEAR, MONTH, DAY);

    public static final String DATE_STRING = LOCAL_DATE.toString();

    public static final String NAME = "Name";

    public static final String SURNAME = "Surname";

    public static final String PHONE_NUMBER = "0888888888";

    public static final String EMAIL = "ab@abv.bg";

    public static final String BOOKS_NAMES_LIST_VALUE = "Test";

    private TestConstants() {
        throw new IllegalStateException("Utility class. Do not instantiate!");
    }
}
