package lk.ijse.therapy_management_system.dao;

import lk.ijse.therapy_management_system.dao.custom.impl.*;


public class DAOFactory {

    private static DAOFactory daoFactory;

    private DAOFactory() {
    }

    public static DAOFactory getInstance() {
        return daoFactory == null ? (daoFactory = new DAOFactory()) : daoFactory;
    }

    @SuppressWarnings("unchecked")
    public <T extends SuperDAO> T getDAO(DAOTypes daoType) {
        return switch (daoType) {
            case USER            -> (T) new UserDAOImpl();
            case PATIENT         -> (T) new PatientDAOImpl();
            case THERAPIST       -> (T) new TherapistDAOImpl();
            case THERAPY_PROGRAM -> (T) new TherapyProgramDAOImpl();
            case THERAPY_SESSION -> (T) new TherapySessionDAOImpl();
            case PAYMENT         -> (T) new PaymentDAOImpl();
        };
    }
}
