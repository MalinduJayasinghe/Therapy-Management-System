package lk.ijse.therapy_management_system.dto;

import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TherapistDTO {
    private String therapistId;
    private String name;
    private String specialization;
    private String email;
    private String phone;
    private String qualification;
    private String availability;
    private boolean active;
}
