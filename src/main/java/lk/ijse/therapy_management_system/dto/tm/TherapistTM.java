package lk.ijse.therapy_management_system.dto.tm;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapistTM {
    private String therapistId;
    private String name;
    private String specialization;
    private String email;
    private String phone;
    private String availability;
}
