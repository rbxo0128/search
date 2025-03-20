package org.example.help.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.help.model.dto.RankDTO;
import org.example.help.model.dto.TFTRankDTO;
import org.example.help.model.dto.puuidResponse;
import org.example.help.service.RiotService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet("/tft/answer")
public class TFTAnswerController extends Controller {
    final static RiotService riotService = RiotService.getInstance();
    final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        String summonerName = req.getParameter("summonerName");
        HttpSession session = req.getSession();

        String summonerResponse = "";
        String puuid = "";
        try {
            summonerResponse = riotService.getSummonerName(summonerName);
            puuid = objectMapper.readValue(summonerResponse, puuidResponse.class).puuid();
            System.out.println("puuid = " + puuid);
            String riotID = riotService.getTFTID(puuid);
            JsonNode root = objectMapper.readTree(riotID);
            String summonerTFTID = root.get("id").asText();
            System.out.println("summonerTFTID = " + summonerTFTID);
            String tftRank = riotService.getTFTRANK(summonerTFTID);
            System.out.println("tftRank = " + tftRank);
            List<TFTRankDTO> rankDataList = objectMapper.readValue(tftRank, new TypeReference<List<TFTRankDTO>>() {});

            req.setAttribute("rankData", rankDataList);

        }catch (Exception e){
            session.setAttribute("error", "소환사 이름을 다시 입력해주세요.");
            System.out.println("summonerName is wrong");
            resp.sendRedirect("/tft");
            return;
        }

        String match = riotService.getTFTMatch(puuid);
        String[] records = objectMapper.readValue(match, String[].class);
        List<List<Map<String, Object>>> matches = new ArrayList<>();

        ExecutorService executor = Executors.newFixedThreadPool(Math.min(records.length, 5)); // 최대 10개 스레드 사용

        try {
            // 각 매치 ID에 대한 Future 리스트 생성
            List<CompletableFuture<List<Map<String, Object>>>> futures = new ArrayList<>();

            for (String record : records) {
                CompletableFuture<List<Map<String, Object>>> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        String matchresult = riotService.getTFTResult(record);
                        JsonNode root = objectMapper.readTree(matchresult);
                        JsonNode info = root.get("info");

                        List<Map<String, Object>> matchData = new ArrayList<>();

                        long gameDatetime = info.path("game_datetime").asLong(0);
                        int gameLength = info.path("game_length").asInt(0);

                        String queueType;
                        switch (info.get("queueId").asInt()) {
                            case 0:
                                queueType = "커스텀";
                                break;
                            case 1090:
                                queueType = "일반";
                                break;

                            case 1100:
                                queueType = "랭크";
                                break;
                            case 1110:
                                queueType = "초고속 모드";
                                break;
                            case 1120:
                                queueType = "더블업";
                                break;
                            case 1130:
                                queueType = "드래곤랜드";
                                break;
                            default:
                                queueType = "기타";
                                break;
                        }

                        for (JsonNode player : info.get("participants")) {
                            Map<String, Object> playerData = new HashMap<>();
                            playerData.put("summoner", player.path("riotIdGameName").asText());
                            playerData.put("summonertag", player.path("riotIdTagline").asText());
                            playerData.put("placement", player.path("placement").asInt()); // 순위
                            playerData.put("level", player.path("level").asInt()); // 레벨

                            // 챔피언 목록 가져오기
                            List<String[]> champions = new ArrayList<>();

                            for (JsonNode unit : player.path("units")) {
                                List<String> champ = new ArrayList<>();
                                champ.add(unit.path("character_id").asText());
                                champ.add(unit.path("tier").asText());
                                champions.add(champ.toArray(new String[0]));
                            }
                            playerData.put("champions", champions);

                            // 공통 게임 정보 추가
                            playerData.put("gameDatetime", gameDatetime);
                            playerData.put("gameLength", gameLength);
                            playerData.put("queueType", queueType);

                            matchData.add(playerData);

                        }

                        matchData.sort(Comparator.comparingInt(m -> (int) m.get("placement")));

                        return matchData;
                    } catch (Exception e) {
                        // 로깅 방식은 환경에 맞게 조정하세요
                        System.err.println("Match data processing error: " + e.getMessage());
                        return new ArrayList<>(); // 에러 발생 시 빈 리스트 반환
                    }
                }, executor);

                futures.add(future);
            }

            // 결과 수집
            for (CompletableFuture<List<Map<String, Object>>> future : futures) {
                try {
                    matches.add(future.get()); // get()은 결과가 완료될 때까지 블로킹됩니다
                } catch (Exception e) {
                    System.err.println("Error getting future result: " + e.getMessage());
                    matches.add(new ArrayList<>()); // 에러 시 빈 리스트 추가
                }
            }

        } catch (Exception e) {
            System.err.println("Error processing match data: " + e.getMessage());
        } finally {
            executor.shutdown(); // 항상 스레드 풀을 종료합니다
        }
        System.out.println(matches);
        req.setAttribute("matches", matches);

        view(req, resp, "tftanswer");
    }
}
