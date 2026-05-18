package lk.ijse.therapy_management_system.dao.custom.impl;

import lk.ijse.therapy_management_system.config.FactoryConfiguration;
import lk.ijse.therapy_management_system.dao.custom.PatientDAO;
import lk.ijse.therapy_management_system.entity.Patient;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.List;
import java.util.Optional;

public class PatientDAOImpl implements PatientDAO {

    @Override
    public List<Patient> getAll() {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            return session.createQuery("FROM Patient", Patient.class).list();
        }
    }

    @Override
    public Optional<Patient> findById(String id) {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            return Optional.ofNullable(session.get(Patient.class, id));
        }
    }

    @Override
    public Optional<Patient> findByNic(String nic) {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            return session.createQuery(
                    "FROM Patient p WHERE p.nic = :nic", Patient.class)
                    .setParameter("nic", nic)
                    .uniqueResultOptional();
        }
    }

    @Override
    public List<Patient> search(String keyword) {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            String search = "%" + keyword + "%";
            return session.createQuery(
                    "FROM Patient p WHERE p.name LIKE :keyword OR p.nic LIKE :keyword OR p.email LIKE :keyword OR p.phone LIKE :keyword",
                    Patient.class)
                    .setParameter("keyword", search)
                    .list();
        }
    }

    @Override
    public List<Patient> findPatientsEnrolledInAllPrograms() {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            String hql =
                    """
                    SELECT DISTINCT ts.patient
                    FROM TherapySession ts
                    GROUP BY ts.patient
                    HAVING COUNT(DISTINCT ts.therapyProgram.programId) =
                           (SELECT COUNT(tp) FROM TherapyProgram tp WHERE tp.active = true)
                    """;
            return session.createQuery(hql, Patient.class).list();
        }
    }

    @Override
    public List<Object[]> findPatientsWithTherapyPrograms() {
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            String hql =
                    """
                    SELECT DISTINCT p, tp
                    FROM Patient p
                    JOIN p.therapySessions ts
                    JOIN ts.therapyProgram tp
                    ORDER BY p.name
                    """;
            return session.createQuery(hql, Object[].class).list();
        }
    }

    @Override
    public boolean save(Patient patient) {
        Transaction transaction = null;
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            transaction = session.beginTransaction();
            session.persist(patient);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction != null) transaction.rollback();
            throw e;
        }
    }

    @Override
    public boolean update(Patient patient) {
        Transaction transaction = null;
        try (Session session = FactoryConfiguration.getInstance().openSession()) {
            transaction = session.beginTransaction();
            session.merge(patient);
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
            Patient patient = session.get(Patient.class, id);
            if (patient != null) {
                session.remove(patient);
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
                    "SELECT p.patientId FROM Patient p ORDER BY p.patientId DESC", String.class)
                    .setMaxResults(1)
                    .uniqueResult();
        }
    }
}
