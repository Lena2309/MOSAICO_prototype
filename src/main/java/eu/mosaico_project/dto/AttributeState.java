package eu.mosaico_project.dto;

import eu.mosaico_project.dto.task.output.value.Value;

import java.util.Map;

/** An Attribute State represents the value of local variables (attibutes). */
public interface AttributeState extends Map<String, Value> { }
