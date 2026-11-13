package dev.vdrenkov.biblium.mapper;

import dev.vdrenkov.biblium.dto.ClientDto;
import dev.vdrenkov.biblium.entity.Client;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public final class ClientMapper {

    private ClientMapper() {
    }

    public static List<ClientDto> mapClientsToClientsDto(final List<Client> clients) {
        final List<ClientDto> clientsDto = new ArrayList<>();

        for (final Client client : clients) {
            clientsDto.add(mapClientToClientDto(client));
        }

        clientsDto.sort(Comparator.comparing(ClientDto::id));
        return clientsDto;
    }

    public static ClientDto mapClientToClientDto(final Client client) {
        return new ClientDto(client.getId(), client.getName(), client.getSurname(), client.getPhoneNumber(),
            client.getEmail());
    }
}
