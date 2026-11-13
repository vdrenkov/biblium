package dev.vdrenkov.biblium.util.factory;

import dev.vdrenkov.biblium.dto.ClientDto;
import dev.vdrenkov.biblium.entity.Client;
import dev.vdrenkov.biblium.request.ClientRequest;

import java.util.Collections;
import java.util.List;

import static dev.vdrenkov.biblium.util.constant.TestConstants.EMAIL;
import static dev.vdrenkov.biblium.util.constant.TestConstants.ID;
import static dev.vdrenkov.biblium.util.constant.TestConstants.NAME;
import static dev.vdrenkov.biblium.util.constant.TestConstants.PHONE_NUMBER;
import static dev.vdrenkov.biblium.util.constant.TestConstants.SURNAME;

public final class ClientFactory {

    private ClientFactory() {
        throw new IllegalStateException("Utility class. Must not be instantiated!");
    }

    public static Client getDefaultClient() {
        return new Client(ID, NAME, SURNAME, PHONE_NUMBER, EMAIL);
    }

    public static List<Client> getDefaultClientsList() {
        return Collections.singletonList(getDefaultClient());
    }

    public static ClientDto getDefaultClientDto() {
        return new ClientDto(ID, NAME, SURNAME, PHONE_NUMBER, EMAIL);
    }

    public static List<ClientDto> getDefaultClientsDtoList() {
        return Collections.singletonList(getDefaultClientDto());
    }

    public static ClientRequest getDefaultClientRequest() {
        return new ClientRequest(NAME, SURNAME, PHONE_NUMBER, EMAIL);
    }
}

