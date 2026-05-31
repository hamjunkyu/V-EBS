package com.douzone.vebs.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "tbl_class")
@Getter
@Setter
@NoArgsConstructor
public class Lesson {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "clsSeq")
    private Long clsSeq;

    @Column(name = "attSeq", nullable = false)
    private Long attSeq;

    @Column(name = "clsNo", nullable = false)
    private Integer clsNo;

    @Column(name = "clsDate", nullable = false)
    private LocalDate clsDate;

    @Column(name = "clsTime", nullable = false)
    private LocalTime clsTime;

    @Column(name = "durationMin", nullable = false)
    private Integer durationMin;

    @Column(name = "tutorId", nullable = false, length = 20)
    private String tutorId;

    @Column(name = "clsStat", nullable = false)
    private Integer clsStat;

    @Column(name = "clsUrl", length = 150)
    private String clsUrl;

    @Column(name = "regDate", nullable = false, insertable = false, updatable = false)
    private LocalDateTime regDate;

    @Column(name = "modDate", nullable = false, insertable = false, updatable = false)
    private LocalDateTime modDate;
}
