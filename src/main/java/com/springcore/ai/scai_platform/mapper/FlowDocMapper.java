package com.springcore.ai.scai_platform.mapper;

import com.springcore.ai.scai_platform.dto.FlowDocDTO;
import com.springcore.ai.scai_platform.entity.FlowDoc;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface FlowDocMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "docId", source = "docId", qualifiedByName = "longToString")
    @Mapping(target = "em", source = "em", qualifiedByName = "longToString")
    @Mapping(target = "owner", source = "owner", qualifiedByName = "longToString")
    @Mapping(target = "activeStepStatus", expression = "java(entity.getIsActiveStep())")
    FlowDocDTO toDto(FlowDoc entity);

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToLong")
    @Mapping(target = "docId", source = "docId", qualifiedByName = "stringToLong")
    @Mapping(target = "em", source = "em", qualifiedByName = "stringToLong")
    @Mapping(target = "owner", source = "owner", qualifiedByName = "stringToLong")
    FlowDoc toEntity(FlowDocDTO dto);

    // Helper Methods สำหรับแปลง Type
    @Named("longToString")
    default String longToString(Long value) {
        return value != null ? value.toString() : null;
    }

    @Named("stringToLong")
    default Long stringToLong(String value) {
        return (value != null && !value.isEmpty()) ? Long.valueOf(value) : null;
    }
}