package com.douzone.vebs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalTime;
import java.time.LocalDateTime;

@Entity
@Table(name="tbl_tutor_timeslot")
@Getter
@Setter
@NoArgsConstructor
public class TutorTimeslot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "slotSeq")
    private Long slotSeq;

    @Column(name = "tutorId", nullable = false, length = 20)
    private String tutorId;

    @Column(name = "dayOfWeek", nullable = false)
    private Integer dayOfWeek;

    @Column(name = "startTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "endTime", nullable = false)
    private LocalTime endTime;

    @Column(name = "regDate", nullable = false, insertable = false, updatable = false)
    private LocalDateTime regDate;
}
