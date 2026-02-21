package com.springcore.ai.scai_platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class FlowDocDTO {

    private String id; // เปลี่ยนจาก Long เป็น String
    private int activeStep;
    private LocalDateTime createdDate;
    private String docId; // เปลี่ยนจาก Long เป็น String
    private String docNo;
    private String docRec;
    private String docType;
    private String em; // เปลี่ยนจาก Long เป็น String
    private String flowBatch;
    private String flowCode;
    private BigDecimal inactive;
    private String lastFlowComment;
    private LocalDateTime lastFlowDate;
    private String lastFlowStat;
    private BigDecimal lastStep;
    private String owner; // เปลี่ยนจาก Long เป็น String
    private LocalDateTime requestedDate;
    private BigDecimal reqCancel;
    private LocalDateTime effDate;
    private String tagsFlowContent;
    private String sysRem;

    private DocumentFormDTO documentForm;
    private List<FlowDocStepDTO> steps; // อย่าลืมสร้าง StepDTO ที่เป็น String ด้วย

    @JsonProperty("isActiveStep")
    private boolean activeStepStatus; // สำหรับรับค่าจาก getIsActiveStep()
}