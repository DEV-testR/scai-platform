package com.springcore.ai.scai_platform.dto;

import com.springcore.ai.scai_platform.entity.Employee;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class FlowDocStepDTO {

    private String id; // เปลี่ยนเป็น String
    private LocalDateTime actionDate;
    private String actionType;
    private String actorType;

    private String emadm; // เปลี่ยนเป็น String
    private String emdeg; // เปลี่ยนเป็น String
    private String emman; // เปลี่ยนเป็น String

    // สำหรับโชว์ข้อมูลพนักงานใน Stepper ที่เราทำกันไว้
    private Employee emmanInfo;

    private BigDecimal isend;
    private String langadm;
    private String langdeg;
    private String langman;
    private String mailadm;
    private String maildeg;
    private String mailman;
    private BigDecimal mailstat;
    private int stepno;
    private Integer isActive;
    private Integer reqCancel;
}