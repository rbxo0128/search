package org.example.help.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.help.model.dto.MatchResultDTO;
import org.example.help.model.dto.RankDTO;
import org.example.help.model.dto.puuidResponse;
import org.example.help.service.GeminiService;
import org.example.help.service.RiotService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@WebServlet("/answer")
public class AnswerController extends Controller {
    final static RiotService riotService = RiotService.getInstance();
    final static ObjectMapper objectMapper = new ObjectMapper();
    final static GeminiService geminiService = new GeminiService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html");
        String summonerName = req.getParameter("summonerName");

        if (summonerName == null || summonerName.trim().isEmpty()) {
            System.out.println("summonerName is null or empty");
            resp.sendRedirect("/");
            return;
        }
        summonerName = summonerName.replace(" ", "");
        log(summonerName);
        String summonerResponse = "";
        String puuid = "";
        String[] records;
        HttpSession session = req.getSession();
        try {
            summonerResponse = riotService.getSummonerName(summonerName);
            System.out.println("session = " + summonerResponse);
            puuid = objectMapper.readValue(summonerResponse, puuidResponse.class).puuid();
            System.out.println("session = " + puuid);
            String rank  = riotService.getRank(puuid);
            List<RankDTO> rankDataList = objectMapper.readValue(rank, new TypeReference<List<RankDTO>>() {});
            System.out.println("rankDataList = " + rankDataList);

            req.setAttribute("rankData", rankDataList);

            String match = riotService.getMatch(puuid);
            records = objectMapper.readValue(match, String[].class);
        }catch (Exception e){
            session.setAttribute("error", "소환사 이름을 다시 입력해주세요.");
            System.out.println("summonerName is wrong");
            resp.sendRedirect("/");
            return;
        }



        List<List<MatchResultDTO>> matches = new ArrayList<>();
        List<String> recentChampions = new ArrayList<>();

// KDA와 CS 저장을 위한 Map
        Map<String, List<Float>> championKdaMap = new HashMap<>();
        Map<String, List<Integer>> championCsMap = new HashMap<>();
// records 배열에 있는 각 매치 ID에 대해 처리
        for (String record : records) {
            try {
                String matchresult = riotService.getMatchResult(record);
                JsonNode root = objectMapper.readTree(matchresult);
                JsonNode info = root.get("info");

                // queueId에 따른 queueType 결정
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

                // participants 배열 추출
                ArrayNode participants = (ArrayNode) info.get("participants");
                String myChampion = null;
                int minionsKilled = 0;
                float mykda = 0;
                // 각 참가자 JSON 객체에 queueType 추가
                for (JsonNode participant : participants) {
                    ((ObjectNode) participant).put("queueType", queueType);

                    if (participant.get("puuid").asText().equalsIgnoreCase(puuid)) {
                        myChampion = participant.get("championName").asText();
                        minionsKilled = participant.get("totalMinionsKilled").asInt() + participant.get("neutralMinionsKilled").asInt();
                        mykda = (float) (participant.get("kills").asInt() + participant.get("assists").asInt()) / Math.max(1, participant.get("deaths").asInt());

                        recentChampions.add(myChampion);

                        // KDA 저장
                        championKdaMap.putIfAbsent(myChampion, new ArrayList<>());
                        championKdaMap.get(myChampion).add(mykda);

                        // CS 저장
                        championCsMap.putIfAbsent(myChampion, new ArrayList<>());
                        championCsMap.get(myChampion).add(minionsKilled);
                    }
                }

                // participants JSON 배열을 문자열로 변환한 후 DTO 리스트로 변환
                List<MatchResultDTO> matchDataList = objectMapper.readValue(
                        participants.toString(), new TypeReference<List<MatchResultDTO>>() {}
                );

                matches.add(matchDataList);
            } catch (Exception e) {
                System.err.println("Error processing match data for record " + record + ": " + e.getMessage());
                // 오류 발생 시 빈 리스트 추가
                matches.add(new ArrayList<>());
            }
        }

        Map<String, Integer> championFrequency = new LinkedHashMap<>();
        for (String champ : recentChampions) {
            championFrequency.put(champ, championFrequency.getOrDefault(champ, 0) + 1);
        }

        List<Map.Entry<String, Integer>> sortedChampions = new ArrayList<>(championFrequency.entrySet());
        sortedChampions.sort((a, b) -> b.getValue() - a.getValue());

// 가장 많이 플레이한 챔피언 Top 3
        List<String> topChampions = sortedChampions.stream()
                .limit(3)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());

// 평균 KDA 및 CS 계산
        float avgKda = 0;
        int avgCs = 0;
        if (!topChampions.isEmpty()) {
            int kdaCount = 0, csCount = 0;
            for (String champ : topChampions) {
                List<Float> kdaList = championKdaMap.getOrDefault(champ, new ArrayList<>());
                List<Integer> csList = championCsMap.getOrDefault(champ, new ArrayList<>());

                avgKda += kdaList.stream().mapToDouble(Float::doubleValue).sum();
                kdaCount += kdaList.size();

                avgCs += csList.stream().mapToInt(Integer::intValue).sum();
                csCount += csList.size();
            }
            avgKda = kdaCount > 0 ? avgKda / kdaCount : 0;
            avgCs = csCount > 0 ? avgCs / csCount : 0;
        }

        String response = geminiService.answerQuestion(topChampions, avgKda, avgCs);
        System.out.println("response = " + response);

        req.setAttribute("response", response);

        req.setAttribute("matches", matches);
        view(req, resp, "answer");
    }
}
