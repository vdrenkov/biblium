package dev.vdrenkov.biblium.controller;

import dev.vdrenkov.biblium.dto.ClientDto;
import dev.vdrenkov.biblium.entity.Client;
import dev.vdrenkov.biblium.request.ClientRequest;
import dev.vdrenkov.biblium.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;

    public ClientController(final ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    public ResponseEntity<Void> addClient(@RequestBody @Valid final ClientRequest clientRequest) {
        final Client client = clientService.addClient(clientRequest);

        final URI location = UriComponentsBuilder.fromUriString("/clients/{id}").buildAndExpand(client.getId()).toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping
    public ResponseEntity<List<ClientDto>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClientsDto());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClientDto> getClientById(@PathVariable final int id) {
        return ResponseEntity.ok(clientService.getClientDtoById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ClientDto> deleteClient(@PathVariable final int id,
        @RequestParam(required = false) final Boolean returnOld) {

        final ClientDto clientDto = clientService.deleteClient(id);

        if (Boolean.TRUE.equals(returnOld)) {
            return ResponseEntity.ok(clientDto);
        } else {
            return ResponseEntity.noContent().build();
        }
    }
}
