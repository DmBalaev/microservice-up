package dm.abs.client_service.repository;

import dm.abs.client_service.entity.Client;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface ClientRepository extends R2dbcRepository<Client, UUID> {

    @Query("""
		INSERT INTO client (client_id, first_name, last_name, middle_name, phone_number, email)
		VALUES (gen_random_uuid(), :#{#client.firstName}, :#{#client.lastName}, 
		        :#{#client.middleName}, :#{#client.phone}, :#{#client.email})
		RETURNING *
		""")
    Mono<Client> create(Client client);

	@Query("""
            UPDATE client SET passport_number = :passportNumber 
            WHERE client_id = :clientId
            """)
	Mono<Void> linkWithPassportData(UUID clientId, String passportNumber);

}
