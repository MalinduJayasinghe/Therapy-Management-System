package lk.ijse.therapy_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "therapy_program")
@AllArgsConstructor
@NoArgsConstructor
@Data

public class TherapyProgram {

    public TherapyProgram(String programId, String programName, String duration, BigDecimal fee, String description, boolean active) {
    }

    @Id
    @Column(name = "program_id", length = 10)
    private String programId;

    @Column(name = "program_name", length = 150, nullable = false)
    private String programName;

    @Column(name = "duration", length = 50, nullable = false)
    private String duration;

    @Column(name = "fee", precision = 10, scale = 2, nullable = false)
    private BigDecimal fee;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    // One TherapyProgram -> Many TherapySessions
    @OneToMany(
            mappedBy = "therapyProgram",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    private List<TherapySession> therapySessions = new ArrayList<>();

    // One Therapist <-> Many TherapyPrograms
    @ManyToMany(mappedBy = "therapyPrograms", fetch = FetchType.LAZY)
    private List<Therapist> therapists = new ArrayList<>();
}
