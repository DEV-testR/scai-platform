package com.springcore.ai.scai_platform.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.springcore.ai.scai_platform.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentSearchResp {
    private Long id;
    private String documentNo;
    private String documentType;
    private int documentStatus;
    private Employee emId;
    private Date documentDate;

    public String getId() {
        return id.toString();
    }

    @JsonProperty("documentStatusLabel")
    public String getDocumentStatusLabel() {
        return switch (documentStatus) {
            case 0 -> "Drafts";
            case 1 -> "Waiting";
            case 2 -> "Approved";
            case 11 -> "Not Approved";
            case 12 -> "Cancel";
            default -> "Unknown";
        };
    }

    @JsonProperty("documentStatusSeverity")
    public String getDocumentStatusSeverity() {
        return switch (documentStatus) {
            case 0 -> "secondary"; // สีเทา
            case 1 -> "warn";      // สีส้ม/เหลือง
            case 2 -> "success";   // สีเขียว
            case 11 -> "danger";   // สีแดง
            case 12 -> "contrast"; // สีดำ/เข้ม
            default -> "info";
        };
    }
}

