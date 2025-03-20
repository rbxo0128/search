package org.example.help.model.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record MatchResultDTO(String riotIdGameName,
                             String riotIdTagline,
                             String championName,
                             int kills,
                             int deaths,
                             int assists,
                             boolean win,
                             int goldEarned,
                             int totalDamageDealtToChampions,
                             String queueType) {
}
