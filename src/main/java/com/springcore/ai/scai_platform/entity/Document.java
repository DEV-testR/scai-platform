package com.springcore.ai.scai_platform.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.springcore.ai.scai_platform.domain.deserialiize.LookupItemToLongDeserializer;
import com.springcore.ai.scai_platform.domain.extend.GenericPersistentObject;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Entity
@Table(name = "am_doc")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document extends GenericPersistentObject {
    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @Column(nullable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "Document_GENERATOR")
    @SequenceGenerator(name = "Document_GENERATOR", sequenceName = "Document_ID_GENERATOR", allocationSize = 1)
    private Long id;

    private String documentNo;

    private String documentType;

    private int documentStatus;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    private Long emId;

    private Date dateWork;

    private Date dateTo;

    private Date punI_D;

    private Date punI_T;

    private Date punO_D;

    private Date punO_T;

    @JsonDeserialize(using = LookupItemToLongDeserializer.class)
    private Long reasonId;

    private String remark;

    public List<DocumentFile> getAttachment() {
        return attachment != null ? attachment : Collections.emptyList();
    }

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "docId")
    private List<DocumentFile> attachment;

}
