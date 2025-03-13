package org.example.help.model.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.help.model.dto.APIClientParam;
import org.example.help.model.dto.RiotAPIParam;
import org.example.help.util.APIClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RiotRepository implements APIClient {
    private RiotRepository() {}
    private static final RiotRepository instance = new RiotRepository();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    public static RiotRepository getInstance() {
        return instance;
    }

    public String callAPI(RiotAPIParam param) throws JsonProcessingException {
        String url = "";
        String token = dotenv.get("RIOT_KEY");
        String method = "GET";
        String[] headers = new String[] {
                "User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/134.0.0.0 Safari/537.36",
                "Accept-Language", "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7",
                "Accept-Charset", "application/x-www-form-urlencoded; charset=UTF-8",
                "Origin", "https://developer.riotgames.com",
                "X-Riot-Token", token
        };


        switch (param.modelType()){
            case SUMMONERNAME:
                String nickname = param.name().split("#")[0];
                String tag = param.name().split("#")[1];

                String nicknameEncoded = URLEncoder.encode(nickname, StandardCharsets.UTF_8);
                String tagEncoded = URLEncoder.encode(tag, StandardCharsets.UTF_8);

                url = "https://asia.api.riotgames.com/riot/account/v1/accounts/by-riot-id/%s/%s".formatted(nicknameEncoded, tagEncoded);

                break;

            case MATCH:
                url = "https://asia.api.riotgames.com/lol/match/v5/matches/by-puuid/%s/ids?start=0&count=10".formatted(param.name());

                break;

            case MATCHID:
                url = "https://asia.api.riotgames.com/lol/match/v5/matches/%s".formatted(param.name());

                break;

            default:

        }

        return APIClient.super.callAPI(new APIClientParam(url, token, method, headers));
    }
}
