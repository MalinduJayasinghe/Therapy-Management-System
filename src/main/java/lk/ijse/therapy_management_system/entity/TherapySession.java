package lk.ijse.therapy_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;


@Entity
@Table(name = "therapy_session")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class TherapySession {

    @Id
    @Column(name = "session_id", length = 10)
    private String sessionId;

    // Many TherapySessions -> One Patient
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    // Many TherapySessions -> One Therapist
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "therapist_id", nullable = false)
    private Therapist therapist;

    // Many TherapySessions -> One TherapyProgram
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "program_id", nullable = false)
    private TherapyProgram therapyProgram;

    @Column(name = "session_date", nullable = false)
    private LocalDate sessionDate;

    @Column(name = "session_time", nullable = false)
    private LocalTime sessionTime;

    @Column(name = "registration_date", nullable = false)
    private LocalDate registrationDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private SessionStatus status = SessionStatus.SCHEDULED;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    // One TherapySession -> One Payment (cascade to payment when session persisted)
    @OneToOne(mappedBy = "therapySession", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Payment payment;

    public enum SessionStatus {
        SCHEDULED,
        COMPLETED,
        CANCELLED,
        RESCHEDULED
    }
}
