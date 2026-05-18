package lk.ijse.therapy_management_system.dto.tm;

import lk.ijse.therapy_management_system.entity.TherapySession;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapySessionTM {
    private String sessionId;
    private String patientName;
    private String therapistName;
    private String programName;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private TherapySession.SessionStatus status;
}
