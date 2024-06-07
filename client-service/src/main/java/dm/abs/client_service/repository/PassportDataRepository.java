package dm.abs.client_service.repository;

import dm.abs.client_service.entity.PassportData;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface PassportDataRepository extends R2dbcRepository<PassportData, String> {

    @Query("""
        INSERT INTO passport_data (passport_number, birthdate)
        VALUES (:#{#passportData.passportNumber}, :#{#passportData.birthday})
        RETURNING *
        """)
    Mono<PassportData> create(PassportData passportData);

    @Query("""
            UPDATE passport_data SET client_id = :clientId 
            WHERE passport_number = :passportNumber
            """)
    Mono<Void> linkWithClient(UUID clientId, String passportNumber);
}
