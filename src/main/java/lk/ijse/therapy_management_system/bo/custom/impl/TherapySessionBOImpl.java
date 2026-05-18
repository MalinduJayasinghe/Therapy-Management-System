package lk.ijse.therapy_management_system.bo.custom.impl;

import lk.ijse.therapy_management_system.bo.custom.TherapySessionBO;
import lk.ijse.therapy_management_system.dao.DAOFactory;
import lk.ijse.therapy_management_system.dao.DAOTypes;
import lk.ijse.therapy_management_system.dao.custom.PatientDAO;
import lk.ijse.therapy_management_system.dao.custom.TherapistDAO;
import lk.ijse.therapy_management_system.dao.custom.TherapyProgramDAO;
import lk.ijse.therapy_management_system.dao.custom.TherapySessionDAO;
import lk.ijse.therapy_management_system.dto.TherapySessionDTO;
import lk.ijse.therapy_management_system.entity.*;
import lk.ijse.therapy_management_system.exception.NotFoundException;
import lk.ijse.therapy_management_system.exception.ScheduleConflictException;
import lk.ijse.therapy_management_system.exception.ValidationException;
import lk.ijse.therapy_management_system.util.EntityDTOConverter;

import java.util.List;
import java.util.stream.Collectors;

public class TherapySessionBOImpl implements TherapySessionBO {

    private final TherapySessionDAO sessionDAO   = DAOFactory.getInstance().getDAO(DAOTypes.THERAPY_SESSION);
    private final PatientDAO        patientDAO   = DAOFactory.getInstance().getDAO(DAOTypes.PATIENT);
    private final TherapistDAO      therapistDAO = DAOFactory.getInstance().getDAO(DAOTypes.THERAPIST);
    private final TherapyProgramDAO programDAO   = DAOFactory.getInstance().getDAO(DAOTypes.THERAPY_PROGRAM);
    private final EntityDTOConverter converter   = new EntityDTOConverter();

    @Override
    public List<TherapySessionDTO> getAllSessions() {
        return sessionDAO.getAll().stream()
                .map(converter::toTherapySessionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void bookSession(TherapySessionDTO dto) {
        validateSessionFields(dto);

        Patient patient = patientDAO.findById(dto.getPatientId())
                .orElseThrow(() -> new NotFoundException("Patient not found: " + dto.getPatientId()));
        Therapist therapist = therapistDAO.findById(dto.getTherapistId())
                .orElseThrow(() -> new NotFoundException("Therapist not found: " + dto.getTherapistId()));
        TherapyProgram program = programDAO.findById(dto.getProgramId())
                .orElseThrow(() -> new NotFoundException("Therapy program not found: " + dto.getProgramId()));

        if (sessionDAO.hasConflict(dto.getTherapistId(), dto.getSessionDate(), dto.getSessionTime())) {
            throw new ScheduleConflictException(
                    "Therapist " + therapist.getName() + " is already booked on "
                    + dto.getSessionDate() + " at " + dto.getSessionTime() + ".");
        }

        TherapySession session = new TherapySession();
        session.setSessionId(dto.getSessionId());
        session.setPatient(patient);
        session.setTherapist(therapist);
        session.setTherapyProgram(program);
        session.setSessionDate(dto.getSessionDate());
        session.setSessionTime(dto.getSessionTime());
        session.setRegistrationDate(dto.getRegistrationDate());
        session.setStatus(dto.getStatus() != null ? dto.getStatus() : TherapySession.SessionStatus.SCHEDULED);
        session.setNotes(dto.getNotes());

        sessionDAO.save(session);
    }

    @Override
    public void updateSession(TherapySessionDTO dto) {
        TherapySession existing = sessionDAO.findById(dto.getSessionId())
                .orElseThrow(() -> new NotFoundException("Session not found: " + dto.getSessionId()));

        validateSessionFields(dto);

        Therapist therapist = therapistDAO.findById(dto.getTherapistId())
                .orElseThrow(() -> new NotFoundException("Therapist not found."));

        // Only check conflict if date/time/therapist changed
        boolean dateChanged    = !existing.getSessionDate().equals(dto.getSessionDate());
        boolean timeChanged    = !existing.getSessionTime().equals(dto.getSessionTime());
        boolean therapistChanged = !existing.getTherapist().getTherapistId().equals(dto.getTherapistId());

        if ((dateChanged || timeChanged || therapistChanged)
                && sessionDAO.hasConflict(dto.getTherapistId(), dto.getSessionDate(), dto.getSessionTime())) {
            throw new ScheduleConflictException(
                    "Therapist " + therapist.getName() + " is already booked at that time.");
        }

        existing.setSessionDate(dto.getSessionDate());
        existing.setSessionTime(dto.getSessionTime());
        existing.setStatus(dto.getStatus());
        existing.setNotes(dto.getNotes());

        sessionDAO.update(existing);
    }

    @Override
    public boolean cancelSession(String sessionId) {
        TherapySession session = sessionDAO.findById(sessionId)
                .orElseThrow(() -> new NotFoundException("Session not found: " + sessionId));
        session.setStatus(TherapySession.SessionStatus.CANCELLED);
        return sessionDAO.update(session);
    }

    @Override
    public String getNextId() {
        String lastId = sessionDAO.getLastId();
        if (lastId != null) {
            int num = Integer.parseInt(lastId.substring(2));
            return String.format("SS%03d", num + 1);
        }
        return "SS001";
    }

    @Override
    public List<TherapySessionDTO> getSessionsByPatient(String patientId) {
        return sessionDAO.findByPatient(patientId).stream()
                .map(converter::toTherapySessionDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<TherapySessionDTO> getSessionsByTherapist(String therapistId) {
        return sessionDAO.findByTherapist(therapistId).stream()
                .map(converter::toTherapySessionDTO)
                .collect(Collectors.toList());
    }

    private void validateSessionFields(TherapySessionDTO dto) {
        if (dto.getPatientId() == null || dto.getPatientId().isBlank()) {
            throw new ValidationException("Patient is required.");
        }
        if (dto.getTherapistId() == null || dto.getTherapistId().isBlank()) {
            throw new ValidationException("Therapist is required.");
        }
        if (dto.getProgramId() == null || dto.getProgramId().isBlank()) {
            throw new ValidationException("Therapy program is required.");
        }
        if (dto.getSessionDate() == null) {
            throw new ValidationException("Session date is required.");
        }
        if (dto.getSessionTime() == null) {
            throw new ValidationException("Session time is required.");
        }
    }
}
