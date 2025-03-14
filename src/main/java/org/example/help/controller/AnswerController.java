package org.example.help.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.help.model.dto.puuidResponse;
import org.example.help.service.RiotService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@WebServlet("/answer")
public class AnswerController extends Controller {
    final static RiotService riotService = RiotService.getInstance();
    final static ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        String summonerName = req.getParameter("summonerName");

        if (summonerName == null || summonerName.trim().isEmpty()) {
            System.out.println("summonerName is null or empty");
            resp.sendRedirect(req.getContextPath() + "/");
            return;
        }

        log(summonerName);
        String summonerResponse = "";
        String puuid = "";
        String[] records;
        HttpSession session = req.getSession();
        try {
            summonerResponse = riotService.getSummonerName(summonerName);
            puuid = objectMapper.readValue(summonerResponse, puuidResponse.class).puuid();
            String rank  = riotService.getRank(puuid);

            req.setAttribute("rank", rank);
            JsonNode root = objectMapper.readTree(rank);

            List<Map<String, Object>> rankData = new ArrayList<>();
            if (root != null && root.isArray()) {
                for (JsonNode node : root) {
                    Map<String, Object> map = new HashMap<>();
                    String queue = "";
                    if (node.get("queueType").asText().equals("RANKED_SOLO_5x5")){
                        queue = "솔로랭크";
                    }
                    else if(node.get("queueType").asText().equals("RANKED_FLEX_SR")){
                        queue = "자유랭크";
                    }
                    else{
                        queue = "기타";
                    }
                    map.put("queueType", queue);
                    map.put("tier", node.get("tier").asText());
                    map.put("rank", node.get("rank").asText());
                    map.put("leaguePoints", node.get("leaguePoints").asInt());
                    map.put("wins", node.get("wins").asInt());
                    map.put("losses", node.get("losses").asInt());
                    rankData.add(map);
                }
            }
            req.setAttribute("rankData", rankData);
            String match = riotService.getMatch(puuid);
            records = objectMapper.readValue(match, String[].class);
        }catch (Exception e){
            session.setAttribute("error", "소환사 이름을 다시 입력해주세요.");
            System.out.println("summonerName is wrong");
            resp.sendRedirect("/");
            return;
        }



        List<List<Map<String, Object>>> matches = new ArrayList<>();

// 스레드 풀 생성
        ExecutorService executor = Executors.newFixedThreadPool(Math.min(records.length, 5)); // 최대 10개 스레드 사용

        try {
            // 각 매치 ID에 대한 Future 리스트 생성
            List<CompletableFuture<List<Map<String, Object>>>> futures = new ArrayList<>();

            for (String record : records) {
                CompletableFuture<List<Map<String, Object>>> future = CompletableFuture.supplyAsync(() -> {
                    try {
                        String matchresult = riotService.getMatchResult(record);
                        JsonNode root = objectMapper.readTree(matchresult);
                        JsonNode info = root.get("info");

                        List<Map<String, Object>> matchData = new ArrayList<>();

                        String queueType;
                        switch (info.get("queueId").asInt()) {
                            case 420:
                                queueType = "솔로 랭크";
                                break;
                            case 440:
                                queueType = "자유 랭크";
                                break;
                            case 400:
                            case 430:
                                queueType = "일반";
                                break;
                            case 450:
                                queueType = "칼바람 나락";
                                break;
                            case 900:
                                queueType = "URF";
                                break;
                            case 1300:
                                queueType = "돌격! 넥서스";
                                break;
                            default:
                                queueType = "기타";
                                break;
                        }

                        for (JsonNode player : info.get("participants")) {
                            Map<String, Object> playerData = new HashMap<>();
                            playerData.put("summoner", player.get("riotIdGameName").asText());
                            playerData.put("summonertag", player.get("riotIdTagline").asText());
                            playerData.put("champ", player.get("championName").asText());
                            playerData.put("kills", player.get("kills").asInt());
                            playerData.put("deaths", player.get("deaths").asInt());
                            playerData.put("assists", player.get("assists").asInt());
                            playerData.put("win", player.get("win").asBoolean());
                            playerData.put("gold", player.get("goldEarned").asInt());
                            playerData.put("damage", player.get("totalDamageDealtToChampions").asInt());
                            playerData.put("queueType", queueType);

                            matchData.add(playerData);
                        }

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

        req.setAttribute("matches", matches);
        view(req, resp, "answer");
    }
}
