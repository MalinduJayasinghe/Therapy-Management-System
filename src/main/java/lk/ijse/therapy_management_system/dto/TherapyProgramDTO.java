package lk.ijse.therapy_management_system.dto;

import lombok.*;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class TherapyProgramDTO {
    private String programId;
    private String programName;
    private String duration;
    private BigDecimal fee;
    private String description;
    private boolean active;
}
