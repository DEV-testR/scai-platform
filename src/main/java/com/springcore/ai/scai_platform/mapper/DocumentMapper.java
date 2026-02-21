package com.springcore.ai.scai_platform.mapper;

import com.springcore.ai.scai_platform.dto.DocumentFormDTO;
import com.springcore.ai.scai_platform.dto.LookupResponse;
import com.springcore.ai.scai_platform.entity.Document;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface DocumentMapper {
    // แปลง Entity -> DTO
    @Mapping(target = "emId", source = "emId")
    @Mapping(target = "reasonId", source = "reasonId")
    @Mapping(target = "documentStatus", expression = "java(com.springcore.ai.scai_platform.domain.constant.DocumentStatus.fromValue(entity.getDocumentStatus()))")
    DocumentFormDTO toDto(Document entity);

    // แปลง DTO -> Entity
    @Mapping(target = "emId", source = "emId")
    @Mapping(target = "reasonId", source = "reasonId")
    @Mapping(target = "documentStatus", expression = "java(dto.getDocumentStatus().getValue())")
    Document toEntity(DocumentFormDTO dto);

    // --- Custom Mapping Methods ---

    // แปลง Long เป็น LookupResponse (Entity -> DTO)
    default LookupResponse mapLongToLookup(Long value) {
        if (value == null) return null;
        return LookupResponse.builder()
                .id(Long.valueOf(value.toString()))
                // หมายเหตุ: ชื่อ name อาจจะว่างเพราะต้องไปหาเพิ่มจาก DB
                // แต่เบื้องต้นใส่ ID เพื่อให้ UI ทำงานได้ก่อน
                .name(value.toString())
                .build();
    }

    // แปลง LookupResponse เป็น Long (DTO -> Entity)
    default Long mapLookupToLong(LookupResponse value) {
        if (value == null || value.getId() == null || value.getId().isEmpty()) {
            return null;
        }
        try {
            return Long.valueOf(value.getId());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}