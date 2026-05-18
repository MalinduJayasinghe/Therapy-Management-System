package lk.ijse.therapy_management_system.dao.custom;

import lk.ijse.therapy_management_system.dao.CrudDAO;
import lk.ijse.therapy_management_system.entity.Patient;

import java.util.List;
import java.util.Optional;

public interface PatientDAO extends CrudDAO<Patient, String> {
    Optional<Patient> findByNic(String nic);
    List<Patient> search(String keyword);
    List<Patient> findPatientsEnrolledInAllPrograms();
    List<Object[]> findPatientsWithTherapyPrograms();
}
