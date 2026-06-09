package eu.mosaico_project.miol;

import eu.mosaico_project.miol.task.output.value.Value;

import java.util.Map;

/**
 * An Attribute State represents the value of local variables (attibutes).
 */
public interface AttributeState extends Map<String, Value> {
}
