package dev.vdrenkov.biblium.service;

import dev.vdrenkov.biblium.dto.ClientDto;
import dev.vdrenkov.biblium.entity.Client;
import dev.vdrenkov.biblium.exception.ClientNotFoundException;
import dev.vdrenkov.biblium.mapper.ClientMapper;
import dev.vdrenkov.biblium.repository.ClientRepository;
import dev.vdrenkov.biblium.request.ClientRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClientService {
    private static final Logger log = LoggerFactory.getLogger(ClientService.class);

    private final ClientRepository clientRepository;

    public ClientService(final ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client addClient(final ClientRequest clientRequest) {
        final Client client = new Client(clientRequest.getName(), clientRequest.getSurname(),
            clientRequest.getPhoneNumber(), clientRequest.getEmail());

        log.info("Trying to add a new client");
        return clientRepository.save(client);
    }

    public List<Client> getAllClients() {
        log.info("Trying to retrieve all clients");
        return clientRepository.findAll();
    }

    public List<ClientDto> getAllClientsDto() {
        return ClientMapper.mapClientsToClientsDto(getAllClients());
    }

    public Client getClientById(final int id) {
        log.info("Trying to retrieve client with an ID {}", id);
        return clientRepository.findById(id).orElseThrow(ClientNotFoundException::new);
    }

    public ClientDto getClientDtoById(final int id) {
        return ClientMapper.mapClientToClientDto(getClientById(id));
    }

    public ClientDto deleteClient(final int id) {
        final ClientDto clientDto = getClientDtoById(id);

        clientRepository.deleteById(id);

        return clientDto;
    }
}
