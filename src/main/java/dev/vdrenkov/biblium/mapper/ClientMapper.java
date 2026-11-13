package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.ClientDto;
import dev.vdrenkov.biblium.entity.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ClientMapper {
    private static final Logger log = LoggerFactory.getLogger(ClientMapper.class);

    private ClientMapper() {
        /* This utility class should not be instantiated */
    }

    public static List<ClientDto> mapClientsToClientsDto(final List<Client> clients) {
        final List<ClientDto> clientsDto = new ArrayList<>();

        for (final Client client : clients) {
            clientsDto.add(mapClientToClientDto(client));
        }

        clientsDto.sort(Comparator.comparing(ClientDto::getId));
        log.debug("Clients' list mapped to clients' DTOs list");
        return clientsDto;
    }

    public static ClientDto mapClientToClientDto(final Client client) {
        log.debug("Client mapped to client DTO");
        return new ClientDto(client.getId(), client.getName(), client.getSurname(), client.getPhoneNumber(),
            client.getEmail());
    }
}
