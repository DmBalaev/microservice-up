package dm.abs.client_service.service;

import dm.abs.client_service.dto.RegistrationClientDto;
import dm.abs.client_service.dto.RegistrationResponse;
import reactor.core.publisher.Mono;

public interface ClientService {
    Mono<RegistrationResponse> createClient(RegistrationClientDto request);
}
