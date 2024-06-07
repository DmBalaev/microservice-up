package dm.abs.client_service.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Client {
    @Id
    private UUID clientId;
    private String firstName;
    private String lastName;
    private String middleName;
    private String phone;
    private String email;
    private PassportData passportData;
}
