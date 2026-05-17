package lk.ijse.therapy_management_system.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "therapist")
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Therapist {

    @Id
    @Column(name = "therapist_id", length = 10)
    private String therapistId;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "specialization", length = 100)
    private String specialization;

    @Column(name = "email", length = 100, unique = true, nullable = false)
    private String email;

    @Column(name = "phone", length = 15, nullable = false)
    private String phone;

    @Column(name = "qualification", length = 200)
    private String qualification;

    @Column(name = "availability", length = 50)
    private String availability;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @OneToMany(
            mappedBy = "therapist",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    private List<TherapySession> therapySessions = new ArrayList<>();

    @ManyToMany(
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            fetch = FetchType.LAZY
    )
    @JoinTable(
            name = "therapist_program",
            joinColumns = @JoinColumn(name = "therapist_id"),
            inverseJoinColumns = @JoinColumn(name = "program_id")
    )

    private List<TherapyProgram> therapyPrograms = new ArrayList<>();
}
