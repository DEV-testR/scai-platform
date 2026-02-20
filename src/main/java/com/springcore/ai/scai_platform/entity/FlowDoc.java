package com.springcore.ai.scai_platform.entity;

import com.springcore.ai.scai_platform.dto.DocumentFormDTO;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "am_flowdoc")
@Getter
@Setter
public class FlowDoc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ACTIVESTEP")
    private BigDecimal activeStep;

    @Column(name = "createddate")
    private LocalDateTime createdDate;

    @Column(name = "docid")
    private Long docId;

    @Transient
    private DocumentFormDTO documentForm;

    @Column(name = "docno", unique = true)
    private String docNo;

    @Column(name = "docrec")
    private String docRec;

    @Column(name = "doctype")
    private String docType;

    @Column(name = "em")
    private Long em;

    @Column(name = "flowbatch")
    private String flowBatch;

    @Column(name = "flowcode")
    private String flowCode;

    @Column(name = "INACTIVE")
    private BigDecimal inactive;

    @Column(name = "lastFlowComment", length = 8000)
    private String lastFlowComment;

    @Column(name = "lastflowdate")
    private LocalDateTime lastFlowDate;

    @Column(name = "lastflowstat")
    private String lastFlowStat;

    @Column(name = "LASTSTEP")
    private BigDecimal lastStep;

    @Column(name = "owner")
    private Long owner;

    @Column(name = "requesteddate")
    private LocalDateTime requestedDate;

    @Column(name = "reqcancel")
    private BigDecimal reqCancel;

    @Column(name = "effDate")
    private LocalDateTime effDate;

    @Column(name = "tagsFlowContent", length = 1000)
    private String tagsFlowContent;

    @Column(name = "sysrem", length = 2000)
    private String sysRem;

    @OneToMany(mappedBy = "flowDoc", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<FlowDocStep> steps;
}