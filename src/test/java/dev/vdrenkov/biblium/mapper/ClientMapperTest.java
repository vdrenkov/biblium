package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.ClientDto;
import dev.vdrenkov.biblium.util.factory.ClientFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ClientMapperTest {

    @Test
    void mapClientsToClientsDto_clientList_preservesAllFields() {
        final List<ClientDto> clientsDto = ClientMapper.mapClientsToClientsDto(ClientFactory.getDefaultClientsList());

        assertEquals(ClientFactory.getDefaultClientsDtoList(), clientsDto);
    }

    @Test
    void mapClientToClientDto_client_preservesAllFields() {
        final ClientDto clientDto = ClientMapper.mapClientToClientDto(ClientFactory.getDefaultClient());

        assertEquals(ClientFactory.getDefaultClientDto(), clientDto);
    }
}
