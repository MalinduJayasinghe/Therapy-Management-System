package lk.ijse.therapy_management_system.bo.custom;

import lk.ijse.therapy_management_system.bo.SuperBO;
import lk.ijse.therapy_management_system.dto.TherapySessionDTO;

import java.util.List;

public interface TherapySessionBO extends SuperBO {
    List<TherapySessionDTO> getAllSessions();
    void bookSession(TherapySessionDTO dto);
    void updateSession(TherapySessionDTO dto);
    boolean cancelSession(String sessionId);
    String getNextId();
    List<TherapySessionDTO> getSessionsByPatient(String patientId);
    List<TherapySessionDTO> getSessionsByTherapist(String therapistId);
}
