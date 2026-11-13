package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.ClientDto;
import dev.vdrenkov.biblium.util.factory.ClientFactory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ClientMapperTest {

    @Test
    void testMapClientsToClientsDto() {
        final List<ClientDto> clientsDto = ClientMapper.mapClientsToClientsDto(ClientFactory.getDefaultClientsList());

        assertNotNull(clientsDto);
    }

    @Test
    void testMapClientToClientDto() {
        final ClientDto clientDto = ClientMapper.mapClientToClientDto(ClientFactory.getDefaultClient());

        assertNotNull(clientDto);
    }
}
