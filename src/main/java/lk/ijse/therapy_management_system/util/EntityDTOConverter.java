package lk.ijse.therapy_management_system.util;

import lk.ijse.therapy_management_system.dto.*;
import lk.ijse.therapy_management_system.entity.*;

public class EntityDTOConverter {

    public UserDTO toUserDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getUsername(),
                user.getPassword(),
                user.getRole(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.isActive()
        );
    }

    public User toUser(UserDTO dto) {
        User user = new User();
        user.setUserId(dto.getUserId());
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setActive(dto.isActive());
        return user;
    }

    public PatientDTO toPatientDTO(Patient patient) {
        return new PatientDTO(
                patient.getPatientId(),
                patient.getName(),
                patient.getNic(),
                patient.getDateOfBirth(),
                patient.getGender(),
                patient.getEmail(),
                patient.getPhone(),
                patient.getAddress(),
                patient.getMedicalHistory(),
                patient.getRegistrationDate(),
                patient.getEmergencyContactName(),
                patient.getEmergencyContactPhone()
        );
    }

    public Patient toPatient(PatientDTO dto) {
        Patient patient = new Patient();
        patient.setPatientId(dto.getPatientId());
        patient.setName(dto.getName());
        patient.setNic(dto.getNic());
        patient.setDateOfBirth(dto.getDateOfBirth());
        patient.setGender(dto.getGender());
        patient.setEmail(dto.getEmail());
        patient.setPhone(dto.getPhone());
        patient.setAddress(dto.getAddress());
        patient.setMedicalHistory(dto.getMedicalHistory());
        patient.setRegistrationDate(dto.getRegistrationDate());
        patient.setEmergencyContactName(dto.getEmergencyContactName());
        patient.setEmergencyContactPhone(dto.getEmergencyContactPhone());
        return patient;
    }

    public TherapistDTO toTherapistDTO(Therapist therapist) {
        return new TherapistDTO(
                therapist.getTherapistId(),
                therapist.getName(),
                therapist.getSpecialization(),
                therapist.getEmail(),
                therapist.getPhone(),
                therapist.getQualification(),
                therapist.getAvailability(),
                therapist.isActive()
        );
    }

    public Therapist toTherapist(TherapistDTO dto) {
        Therapist therapist = new Therapist();
        therapist.setTherapistId(dto.getTherapistId());
        therapist.setName(dto.getName());
        therapist.setSpecialization(dto.getSpecialization());
        therapist.setEmail(dto.getEmail());
        therapist.setPhone(dto.getPhone());
        therapist.setQualification(dto.getQualification());
        therapist.setAvailability(dto.getAvailability());
        therapist.setActive(dto.isActive());
        return therapist;
    }

    public TherapyProgramDTO toTherapyProgramDTO(TherapyProgram program) {
        return new TherapyProgramDTO(
                program.getProgramId(),
                program.getProgramName(),
                program.getDuration(),
                program.getFee(),
                program.getDescription(),
                program.isActive()
        );
    }

    public TherapyProgram toTherapyProgram(TherapyProgramDTO dto) {
        TherapyProgram program = new TherapyProgram();
        program.setProgramId(dto.getProgramId());
        program.setProgramName(dto.getProgramName());
        program.setDuration(dto.getDuration());
        program.setFee(dto.getFee());
        program.setDescription(dto.getDescription());
        program.setActive(dto.isActive());
        return program;
    }

    public TherapySessionDTO toTherapySessionDTO(TherapySession session) {
        return new TherapySessionDTO(
                session.getSessionId(),
                session.getPatient().getPatientId(),
                session.getPatient().getName(),
                session.getTherapist().getTherapistId(),
                session.getTherapist().getName(),
                session.getTherapyProgram().getProgramId(),
                session.getTherapyProgram().getProgramName(),
                session.getSessionDate(),
                session.getSessionTime(),
                session.getRegistrationDate(),
                session.getStatus(),
                session.getNotes()
        );
    }

    public PaymentDTO toPaymentDTO(Payment payment) {
        return new PaymentDTO(
                payment.getPaymentId(),
                payment.getTherapySession().getSessionId(),
                payment.getPatient().getPatientId(),
                payment.getPatient().getName(),
                payment.getAmount(),
                payment.getPaymentDate(),
                payment.getPaymentMethod(),
                payment.getStatus(),
                payment.getTransactionRef()
        );
    }
}
