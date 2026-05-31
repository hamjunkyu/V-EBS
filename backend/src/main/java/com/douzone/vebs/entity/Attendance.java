package com.douzone.vebs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tbl_attendance")
@Getter
@Setter
@NoArgsConstructor
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attSeq")
    private Long attSeq;

    @Column(name = "studentId", nullable = false, length = 20)
    private String studentId;

    @Column(name = "studentSeq", nullable = false)
    private Integer studentSeq;

    @Column(name = "startDate", nullable = false)
    private LocalDate startDate;

    @Column(name = "endDate", nullable = false)
    private LocalDate endDate;

    @Column(name = "lessonCount", nullable = false)
    private Integer lessonCount;

    @Column(name = "dayPattern", nullable = false)
    private Integer dayPattern;

    @Column(name = "courseType", nullable = false)
    private Integer courseType;

    @Column(name = "startTime", nullable = false)
    private LocalTime startTime;

    @Column(name = "durationMin", nullable = false)
    private Integer durationMin;

    @Column(name = "tutorId", nullable = false, length = 20)
    private String tutorId;

    @Column(name = "attStat", nullable = false)
    private Integer attStat;

    @Column(name = "regDate", nullable = false, insertable = false, updatable = false)
    private LocalDateTime regDate;

    @Column(name = "modDate", nullable = false, insertable = false, updatable = false)
    private LocalDateTime modDate;
}
