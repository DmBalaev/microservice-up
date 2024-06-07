package unit.service;

import dm.abs.client_service.dto.RegistrationClientDto;
import dm.abs.client_service.dto.RegistrationResponse;
import dm.abs.client_service.entity.Client;
import dm.abs.client_service.entity.PassportData;
import dm.abs.client_service.exception.PassportDataAlreadyExists;
import dm.abs.client_service.exception.UserAlreadyRegistered;
import dm.abs.client_service.repository.ClientRepository;
import dm.abs.client_service.repository.PassportDataRepository;
import dm.abs.client_service.service.impl.ClientServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDate;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private PassportDataRepository passportDataRepository;

    @InjectMocks
    private ClientServiceImpl clientService;

    private RegistrationClientDto registrationClientDto;
    private Client client;
    private PassportData passportData;

    @BeforeEach
    public void setup() {
        registrationClientDto = new RegistrationClientDto(
                "John", "Doe", "Middle", "john.doe@example.com",
                LocalDate.of(1990, 1, 1), "+1234567890", "123456789"
        );

        client = Client.builder()
                .clientId(UUID.randomUUID())
                .firstName("John")
                .lastName("Doe")
                .middleName("Middle")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .build();

        passportData = PassportData.builder()
                .passportNumber("123456789")
                .birthday(LocalDate.of(1990, 1, 1))
                .build();
    }

    @Test
    @DisplayName("Success request")
    void testCreateClient_Success() {
        when(clientRepository.create(any(Client.class))).thenReturn(Mono.just(client));
        when(passportDataRepository.create(any(PassportData.class))).thenReturn(Mono.just(passportData));
        when(passportDataRepository.linkWithClient(any(UUID.class), any(String.class))).thenReturn(Mono.empty());
        when(clientRepository.linkWithPassportData(any(UUID.class), any(String.class))).thenReturn(Mono.empty());

        Mono<RegistrationResponse> result = clientService.createClient(registrationClientDto);

        StepVerifier.create(result)
                .expectNextMatches(response -> response.clientId().equals(client.getClientId()))
                .verifyComplete();

        verify(clientRepository).create(any(Client.class));
        verify(passportDataRepository).create(any(PassportData.class));
        verify(passportDataRepository).linkWithClient(any(UUID.class), any(String.class));
        verify(clientRepository).linkWithPassportData(any(UUID.class), any(String.class));
    }

    @Test
    @DisplayName("Bad email or phone")
    void testCreateClient_UserAlreadyRegistered() {
        when(clientRepository.create(any(Client.class))).thenReturn(Mono.error(new DuplicateKeyException("Duplicate key")));

        Mono<RegistrationResponse> result = clientService.createClient(registrationClientDto);

        StepVerifier.create(result)
                .expectError(UserAlreadyRegistered.class)
                .verify();

        verify(clientRepository).create(any(Client.class));
    }

    @Test
    @DisplayName("Incorrect passport data")
    void testCreateClient_PassportDataAlreadyExists() {
        when(clientRepository.create(any(Client.class))).thenReturn(Mono.just(client));
        when(passportDataRepository.create(any(PassportData.class))).thenReturn(Mono.error(new DuplicateKeyException("Duplicate key")));

        Mono<RegistrationResponse> result = clientService.createClient(registrationClientDto);

        StepVerifier.create(result)
                .expectError(PassportDataAlreadyExists.class)
                .verify();

        verify(clientRepository).create(any(Client.class));
        verify(passportDataRepository).create(any(PassportData.class));
    }
}