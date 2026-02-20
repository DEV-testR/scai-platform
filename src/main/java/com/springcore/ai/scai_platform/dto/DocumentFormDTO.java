package com.springcore.ai.scai_platform.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springcore.ai.scai_platform.domain.deserialiize.LookupItemToLongDeserializer;
import com.springcore.ai.scai_platform.entity.DocumentFile;
import com.springcore.ai.scai_platform.entity.Employee;
import jakarta.persistence.CascadeType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.Date;
import java.util.List;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DocumentFormDTO {
    private String id;

    private String documentNo;

    private String documentType;

    private int documentStatus;

    private LookupResponse emId;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date dateWork;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date dateTo;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date punI_D;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date punI_T;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date punO_D;

    @JsonFormat(shape = JsonFormat.Shape.NUMBER_INT)
    private Date punO_T;

    private LookupResponse reasonId;

    private String remark;

    public List<DocumentFile> getAttachment() {
        return attachment != null ? attachment : Collections.emptyList();
    }

    private List<DocumentFile> attachment;
}

