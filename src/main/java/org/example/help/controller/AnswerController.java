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
            resp.sendRedirect("/");
            return;
        }

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
                // 각 참가자 JSON 객체에 queueType 추가
                for (JsonNode participant : participants) {
                    ((ObjectNode) participant).put("queueType", queueType);
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
        System.out.println("matches = " + matches);
        req.setAttribute("matches", matches);
        view(req, resp, "answer");
    }
}
