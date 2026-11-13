package dev.vdrenkov.biblium.mapper;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static dev.vdrenkov.biblium.util.constant.TestConstants.LOCAL_DATE;

class LocalDateMapperTest {

    @Test
    void testMapStringToDate() {
        final LocalDate response = LocalDateMapper.mapStringToDate("2000-01-01");

        Assertions.assertEquals(LOCAL_DATE.toString(), response.toString());
    }
}

