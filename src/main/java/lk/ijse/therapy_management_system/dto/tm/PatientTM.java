package lk.ijse.therapy_management_system.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PatientTM {
    private String patientId;
    private String name;
    private String nic;
    private String email;
    private String phone;
    private String gender;
    private LocalDate registrationDate;
}
