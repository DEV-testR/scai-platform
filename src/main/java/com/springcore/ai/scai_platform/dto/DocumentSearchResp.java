package com.springcore.ai.scai_platform.dto;

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

    public String getDocumentStatusLabel() {
        return switch (documentStatus) {
            case 0 -> "Drafts";
            case 1 -> "Waiting";
            case 2 -> "Approved";
            case 11 -> "Not Approved";
            case 12 -> "Cancel";
            default -> "";
        };
    }

    public String getDocumentStatusSeverity() {
        return switch (documentStatus) {
            case 0 -> "secondary";
            case 1 -> "warn";
            case 2 -> "success";
            case 11 -> "danger";
            case 12 -> "contrast";
            default -> "";
        };
    }
}

