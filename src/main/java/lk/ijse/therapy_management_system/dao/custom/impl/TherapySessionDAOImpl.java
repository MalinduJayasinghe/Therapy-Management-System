package lk.ijse.therapy_management_system.dao.custom.impl;

import lk.ijse.therapy_management_system.config.FactoryConfiguration;
import lk.ijse.therapy_management_system.dao.custom.TherapySessionDAO;
import lk.ijse.therapy_management_system.entity.TherapySession;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public class TherapySessionDAOImpl implements TherapySessionDAO {

    @Override
    public List<TherapySession> getAll() {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            return session.createQuery(
                    "FROM TherapySession ts JOIN FETCH ts.patient JOIN FETCH ts.therapist JOIN FETCH ts.therapyProgram",
                    TherapySession.class).list();
        }
    }

    @Override
    public Optional<TherapySession> findById(String id) {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            return Optional.ofNullable(session.get(TherapySession.class, id));
        }
    }

    @Override
    public List<TherapySession> findByPatient(String patientId) {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            return session.createQuery(
                    "FROM TherapySession ts WHERE ts.patient.patientId = :pid", TherapySession.class)
                    .setParameter("pid", patientId)
                    .list();
        }
    }

    @Override
    public List<TherapySession> findByTherapist(String therapistId) {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            return session.createQuery(
                    "FROM TherapySession ts WHERE ts.therapist.therapistId = :tid", TherapySession.class)
                    .setParameter("tid", therapistId)
                    .list();
        }
    }

    @Override
    public boolean hasConflict(String therapistId, LocalDate date, LocalTime time) {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(ts) FROM TherapySession ts " +
                    "WHERE ts.therapist.therapistId = :tid " +
                    "AND ts.sessionDate = :date " +
                    "AND ts.sessionTime = :time " +
                    "AND ts.status <> lk.ijse.therapy_management_system.entity.TherapySession$SessionStatus.CANCELLED",
                    Long.class)
                    .setParameter("tid", therapistId)
                    .setParameter("date", date)
                    .setParameter("time", time)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    public boolean patientAlreadyEnrolled(String patientId, String programId) {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            Long count = session.createQuery(
                    "SELECT COUNT(ts) FROM TherapySession ts " +
                    "WHERE ts.patient.patientId = :pid AND ts.therapyProgram.programId = :pgid " +
                    "AND ts.status <> lk.ijse.therapy_management_system.entity.TherapySession$SessionStatus.CANCELLED",
                    Long.class)
                    .setParameter("pid", patientId)
                    .setParameter("pgid", programId)
                    .uniqueResult();
            return count != null && count > 0;
        }
    }

    @Override
    public boolean save(TherapySession session_) {
        Transaction transaction = null;
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            transaction = session.beginTransaction();
            session.persist(session_);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public boolean update(TherapySession session_) {
        Transaction transaction = null;
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            transaction = session.beginTransaction();
            session.merge(session_);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public boolean delete(String id) {
        Transaction transaction = null;
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            transaction = session.beginTransaction();
            TherapySession ts = session.get(TherapySession.class, id);
            if (ts != null) {
                session.remove(ts);
                transaction.commit();
                return true;
            }
            transaction.rollback();
            return false;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public String getLastId() {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            return session.createQuery(
                    "SELECT ts.sessionId FROM TherapySession ts ORDER BY ts.sessionId DESC", String.class)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }
}
