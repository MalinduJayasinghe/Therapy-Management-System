package lk.ijse.therapy_management_system.dto;

import lk.ijse.therapy_management_system.entity.Payment;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class PaymentDTO {
    private String paymentId;
    private String sessionId;
    private String patientId;
    private String patientName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private Payment.PaymentMethod paymentMethod;
    private Payment.PaymentStatus status;
    private String transactionRef;
}
