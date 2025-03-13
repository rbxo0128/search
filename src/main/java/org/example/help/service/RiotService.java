package org.example.help.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.help.model.dto.ModelType;
import org.example.help.model.dto.RiotAPIParam;
import org.example.help.model.repository.RiotRepository;

public class RiotService {
    private RiotService() {}
    private static final RiotService instance = new RiotService();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RiotRepository repository = RiotRepository.getInstance();


    public static RiotService getInstance() {
        return instance;
    }

    public String getSummonerName(String name) throws JsonProcessingException {
        return repository.callAPI(new RiotAPIParam(name, ModelType.SUMMONERNAME));
    }

    public String getMatch(String puuid) throws JsonProcessingException {
        return repository.callAPI(new RiotAPIParam(puuid, ModelType.MATCH));
    }

    public String getMatchResult(String matchID) throws JsonProcessingException {
        return repository.callAPI(new RiotAPIParam(matchID, ModelType.MATCHID));
    }

    public String getTFTMatch(String matchID) throws JsonProcessingException {
        return repository.callAPI(new RiotAPIParam(matchID, ModelType.TFTMATCH));
    }

    public String getTFTResult(String matchID) throws JsonProcessingException {
        return repository.callAPI(new RiotAPIParam(matchID, ModelType.TFTMATCHID));
    }
}
