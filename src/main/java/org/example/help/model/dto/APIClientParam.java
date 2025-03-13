package org.example.help.model.dto;

import java.util.Map;

public record APIClientParam(String url, String token, String method, String[] headers) {

}
