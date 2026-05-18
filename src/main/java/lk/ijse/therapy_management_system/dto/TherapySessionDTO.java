package lk.ijse.therapy_management_system.dto;

import lk.ijse.therapy_management_system.entity.TherapySession;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TherapySessionDTO {
    private String sessionId;
    private String patientId;
    private String patientName;
    private String therapistId;
    private String therapistName;
    private String programId;
    private String programName;
    private LocalDate sessionDate;
    private LocalTime sessionTime;
    private LocalDate registrationDate;
    private TherapySession.SessionStatus status;
    private String notes;
}
