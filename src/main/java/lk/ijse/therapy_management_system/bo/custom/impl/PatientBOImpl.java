package lk.ijse.therapy_management_system.bo.custom.impl;

import lk.ijse.therapy_management_system.bo.custom.PatientBO;
import lk.ijse.therapy_management_system.dao.DAOFactory;
import lk.ijse.therapy_management_system.dao.DAOTypes;
import lk.ijse.therapy_management_system.dao.custom.PatientDAO;
import lk.ijse.therapy_management_system.dao.custom.TherapySessionDAO;
import lk.ijse.therapy_management_system.dto.PatientDTO;
import lk.ijse.therapy_management_system.entity.Patient;
import lk.ijse.therapy_management_system.entity.TherapyProgram;
import lk.ijse.therapy_management_system.exception.DuplicateEntryException;
import lk.ijse.therapy_management_system.exception.NotFoundException;
import lk.ijse.therapy_management_system.exception.ValidationException;
import lk.ijse.therapy_management_system.util.EntityDTOConverter;
import lk.ijse.therapy_management_system.util.ValidationUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientBOImpl implements PatientBO {

    private final PatientDAO patientDAO = DAOFactory.getInstance().getDAO(DAOTypes.PATIENT);
    private final TherapySessionDAO sessionDAO = DAOFactory.getInstance().getDAO(DAOTypes.THERAPY_SESSION);
    private final EntityDTOConverter converter = new EntityDTOConverter();

    @Override
    public List<PatientDTO> getAllPatients() {
        return patientDAO.getAll().stream()
                .map(converter::toPatientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void savePatient(PatientDTO dto) {
        validatePatientFields(dto);

        if (patientDAO.findByNic(dto.getNic()).isPresent()) {
            throw new DuplicateEntryException("A patient with NIC '" + dto.getNic() + "' already exists.");
        }

        Patient patient = converter.toPatient(dto);
        patientDAO.save(patient);
    }

    @Override
    public void updatePatient(PatientDTO dto) {
        Optional<Patient> existing = patientDAO.findById(dto.getPatientId());
        if (existing.isEmpty()) {
            throw new NotFoundException("Patient not found: " + dto.getPatientId());
        }

        validatePatientFields(dto);

        Optional<Patient> byNic = patientDAO.findByNic(dto.getNic());
        if (byNic.isPresent() && !byNic.get().getPatientId().equals(dto.getPatientId())) {
            throw new DuplicateEntryException("NIC '" + dto.getNic() + "' belongs to another patient.");
        }

        Patient patient = converter.toPatient(dto);
        patientDAO.update(patient);
    }

    @Override
    public boolean deletePatient(String patientId) {
        if (patientDAO.findById(patientId).isEmpty()) {
            throw new NotFoundException("Patient not found: " + patientId);
        }
        return patientDAO.delete(patientId);
    }

    @Override
    public String getNextId() {
        String lastId = patientDAO.getLastId();
        if (lastId != null) {
            int num = Integer.parseInt(lastId.substring(1));
            return String.format("P%03d", num + 1);
        }
        return "P001";
    }

    @Override
    public List<PatientDTO> search(String keyword) {
        return patientDAO.search(keyword).stream()
                .map(converter::toPatientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<PatientDTO> getPatientsEnrolledInAllPrograms() {
        return patientDAO.findPatientsEnrolledInAllPrograms().stream()
                .map(converter::toPatientDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<String[]> getPatientsWithTherapyPrograms() {
        return patientDAO.findPatientsWithTherapyPrograms().stream()
                .map(row -> {
                    Patient p  = (Patient) row[0];
                    TherapyProgram tp = (TherapyProgram) row[1];
                    return new String[]{
                            p.getPatientId(),
                            p.getName(),
                            tp.getProgramId(),
                            tp.getProgramName()
                    };
                })
                .collect(Collectors.toList());
    }

    private void validatePatientFields(PatientDTO dto) {

        if (!ValidationUtil.isValidName(dto.getName())) {
            throw new ValidationException("Invalid patient name.");
        }
        if (!ValidationUtil.isValidNic(dto.getNic())) {
            throw new ValidationException("Invalid NIC format.");
        }
        if (!ValidationUtil.isValidEmail(dto.getEmail())) {
            throw new ValidationException("Invalid email format.");
        }
        if (!ValidationUtil.isValidPhone(dto.getPhone())) {
            throw new ValidationException("Invalid phone number (Sri Lanka format required).");
        }
    }
}
