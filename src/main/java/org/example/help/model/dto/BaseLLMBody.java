package org.example.help.model.dto;

import java.util.List;
import java.util.Map;

public record BaseLLMBody(List<Map<String, String>> messages) {

}
