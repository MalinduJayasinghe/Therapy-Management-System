package lk.ijse.therapy_management_system.dto;

import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PatientDTO {
    private String patientId;
    private String name;
    private String nic;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String address;
    private String medicalHistory;
    private LocalDate registrationDate;
    private String emergencyContactName;
    private String emergencyContactPhone;
}
