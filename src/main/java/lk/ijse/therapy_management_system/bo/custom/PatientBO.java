package lk.ijse.therapy_management_system.bo.custom;

import lk.ijse.therapy_management_system.bo.SuperBO;
import lk.ijse.therapy_management_system.dto.PatientDTO;

import java.util.List;

public interface PatientBO extends SuperBO {
    List<PatientDTO> getAllPatients();
    void savePatient(PatientDTO dto);
    void updatePatient(PatientDTO dto);
    boolean deletePatient(String patientId);
    String getNextId();
    List<PatientDTO> search(String keyword);
    List<PatientDTO> getPatientsEnrolledInAllPrograms();
    List<String[]> getPatientsWithTherapyPrograms();
}
