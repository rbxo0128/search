package org.example.help.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TFTRankDTO(String queueType, String tier, String rank, int leaguePoints, int wins, int losses) {
}
