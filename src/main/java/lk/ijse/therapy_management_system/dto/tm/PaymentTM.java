package lk.ijse.therapy_management_system.dto.tm;

import lk.ijse.therapy_management_system.entity.Payment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PaymentTM {
    private String paymentId;
    private String patientName;
    private String programName;
    private BigDecimal amount;
    private LocalDate paymentDate;
    private Payment.PaymentMethod paymentMethod;
    private Payment.PaymentStatus status;
}
