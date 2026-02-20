package com.springcore.ai.scai_platform.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "am_flowdocstep")
@Getter
@Setter
public class FlowDocStep {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "actiondate")
    private LocalDateTime actionDate;

    @Column(name = "actiontype")
    private String actionType;

    @Column(name = "actortype")
    private String actorType;

    private Long emadm;
    private Long emdeg;
    private Long emman;

    @Transient
    private Employee emmanInfo;

    @Column(nullable = false)
    private BigDecimal isend;

    private String langadm;
    private String langdeg;
    private String langman;

    private String mailadm;
    private String maildeg;
    private String mailman;

    @Column(nullable = false)
    private BigDecimal mailstat;

    @Column(nullable = false)
    private BigDecimal stepno;

    @Column(name = "isActive")
    private Integer isActive;

    @Column(name = "reqCancel")
    private Integer reqCancel;

    // เชื่อมกลับไปยัง Parent
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private FlowDoc flowDoc;
}