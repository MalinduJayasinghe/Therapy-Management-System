package lk.ijse.therapy_management_system.bo;

import lk.ijse.therapy_management_system.bo.custom.impl.*;

public class BOFactory {

    private static BOFactory boFactory;

    private BOFactory() {
    }

    public static BOFactory getInstance() {
        return boFactory == null ? (boFactory = new BOFactory()) : boFactory;
    }

    public <T extends SuperBO> T getBO(BOTypes boType) {
        return switch (boType) {
            case USER -> (T) new UserBOImpl();
            case PATIENT -> (T) new PatientBOImpl();
            case THERAPIST -> (T) new TherapistBOImpl();
            case THERAPY_PROGRAM -> (T) new TherapyProgramBOImpl();
            case THERAPY_SESSION -> (T) new TherapySessionBOImpl();
            case PAYMENT -> (T) new PaymentBOImpl();
        };
    }
}
