package com.springcore.ai.scai_platform.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "am_reason")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reason {
    @Id
    @Column(name = "id", precision = 19)
    private Long id; // decimal(19,0) ใน DB มักใช้ Long ใน Java

    private String code;

    private String name;

    private String nameAlt;

    @Lob // สำหรับ Data Type: text
    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(precision = 10)
    private Integer inactive; // decimal(10,0) ใช้ Integer ได้ถ้าเก็บแค่ 0/1

    private String refCode;

    private String reasonTyp;

    @Column(precision = 19)
    private Long patId;

    private String tagsValue;
}
