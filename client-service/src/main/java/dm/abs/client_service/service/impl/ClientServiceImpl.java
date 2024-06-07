package dm.abs.client_service.service.impl;

import dm.abs.client_service.dto.RegistrationClientDto;
import dm.abs.client_service.dto.RegistrationResponse;
import dm.abs.client_service.entity.Client;
import dm.abs.client_service.entity.PassportData;
import dm.abs.client_service.exception.PassportDataAlreadyExists;
import dm.abs.client_service.exception.UserAlreadyRegistered;
import dm.abs.client_service.repository.ClientRepository;
import dm.abs.client_service.repository.PassportDataRepository;
import dm.abs.client_service.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;
    private final PassportDataRepository passportDataRepository;

    @Override
    @Transactional
    public Mono<RegistrationResponse> createClient(RegistrationClientDto request) {
        return Mono.defer(() -> {
            Client newClient = buildClient(request);
            PassportData passportData = buildPassportData(request);

            return clientRepository.create(newClient)
                    .onErrorMap(DuplicateKeyException.class, ex -> new UserAlreadyRegistered(" "))
                    .flatMap(client -> createPassportData(passportData, client));
        });
    }

    private Mono<RegistrationResponse> createPassportData(PassportData passportData, Client client) {
        return passportDataRepository.create(passportData)
                .onErrorMap(DuplicateKeyException.class, ex -> new PassportDataAlreadyExists(" "))
                .flatMap(passport -> linkClientAndPassport(client.getClientId(), passport.getPassportNumber()));
    }

    private Mono<RegistrationResponse> linkClientAndPassport(UUID clientId, String passportNumber) {
        return passportDataRepository.linkWithClient(clientId, passportNumber)
                .then(clientRepository.linkWithPassportData(clientId, passportNumber))
                .then(Mono.just(new RegistrationResponse(clientId)));
    }

    private Client buildClient(RegistrationClientDto dto) {
        return Client.builder()
                .firstName(dto.firstName())
                .lastName(dto.lastName())
                .middleName(dto.middleName())
                .email(dto.email())
                .phone(dto.phone())
                .build();
    }

    private PassportData buildPassportData(RegistrationClientDto dto) {
        return PassportData.builder()
                .passportNumber(dto.passportNumber())
                .birthday(dto.birthDate())
                .build();
    }

}
