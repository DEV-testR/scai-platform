package com.springcore.ai.scai_platform.mapper;

import com.springcore.ai.scai_platform.dto.FlowDocStepDTO;
import com.springcore.ai.scai_platform.entity.FlowDocStep;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

public interface FlowDocStepMapper {

    @Mapping(target = "id", source = "id", qualifiedByName = "longToString")
    @Mapping(target = "emadm", source = "emadm", qualifiedByName = "longToString")
    @Mapping(target = "emdeg", source = "emdeg", qualifiedByName = "longToString")
    @Mapping(target = "emman", source = "emman", qualifiedByName = "longToString")
    FlowDocStepDTO toDto(FlowDocStep entity);

    @Mapping(target = "id", source = "id", qualifiedByName = "stringToLong")
    @Mapping(target = "emadm", source = "emadm", qualifiedByName = "stringToLong")
    @Mapping(target = "emdeg", source = "emdeg", qualifiedByName = "stringToLong")
    @Mapping(target = "emman", source = "emman", qualifiedByName = "stringToLong")
    @Mapping(target = "flowDoc", ignore = true) // ป้องกัน Circular Dependency
    FlowDocStep toEntity(FlowDocStepDTO dto);

    @Named("longToString")
    default String longToString(Long value) {
        return value != null ? value.toString() : null;
    }

    @Named("stringToLong")
    default Long stringToLong(String value) {
        return (value != null && !value.isEmpty()) ? Long.valueOf(value) : null;
    }
}