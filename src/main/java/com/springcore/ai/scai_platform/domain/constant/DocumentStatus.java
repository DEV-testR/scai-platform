package com.springcore.ai.scai_platform.domain.constant;

import lombok.Getter;

@Getter
public enum DocumentStatus {
    DRAFT(0),
    WAITING(1),
    APPROVED(2),
    NOT_APPROVED(11),
    CANCEL(12);

    private final int value;

    // Constructor รับแค่ value ตามที่คุณต้องการ
    DocumentStatus(int value) {
        this.value = value;
    }

    // Method ดึง Label โดยใช้ switch จากตัวมันเอง (this)
    public String getLabel() {
        return switch (this) {
            case DRAFT -> "Drafts";
            case WAITING -> "Waiting";
            case APPROVED -> "Approved";
            case NOT_APPROVED -> "Not Approved";
            case CANCEL -> "Cancel";
        };
    }

    // Method ดึง Severity โดยใช้ switch จากตัวมันเอง (this)
    public String getSeverity() {
        return switch (this) {
            case DRAFT -> "secondary";
            case WAITING -> "warn";
            case APPROVED -> "success";
            case NOT_APPROVED -> "danger";
            case CANCEL -> "contrast";
        };
    }

    // Static Method สำหรับแปลงค่า int จาก DB ให้เป็น Object Enum
    public static DocumentStatus fromValue(int value) {
        for (DocumentStatus status : DocumentStatus.values()) {
            if (status.value == value) {
                return status;
            }
        }
        return DRAFT; // default
    }
}