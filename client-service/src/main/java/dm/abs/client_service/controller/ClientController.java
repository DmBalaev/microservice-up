package dm.abs.client_service.controller;

import dm.abs.client_service.dto.RegistrationClientDto;
import dm.abs.client_service.dto.RegistrationResponse;
import dm.abs.client_service.service.ClientService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/clients")
public class ClientController {
    private final ClientService clientService;

    @PostMapping
    public Mono<ResponseEntity<RegistrationResponse>> create(@RequestBody @Valid RegistrationClientDto request){
        return clientService.createClient(request)
                .map(r -> ResponseEntity.status(HttpStatus.CREATED).body(r));
    }
}
