package lk.ijse.therapy_management_system.dao.custom;

import lk.ijse.therapy_management_system.dao.CrudDAO;
import lk.ijse.therapy_management_system.entity.TherapySession;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface TherapySessionDAO extends CrudDAO<TherapySession, String> {
    List<TherapySession> findByPatient(String patientId);
    List<TherapySession> findByTherapist(String therapistId);
    boolean hasConflict(String therapistId, LocalDate date, LocalTime time);
    boolean patientAlreadyEnrolled(String patientId, String programId);
}
