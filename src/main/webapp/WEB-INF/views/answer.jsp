<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>LoL 전적 검색</title>
    <link href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css" rel="stylesheet">
    <style>
        :root {
            --primary: #0A1428;
            --secondary: #091428;
            --accent: #C89B3C;
            --win: #28a745;
            --lose: #dc3545;
            --text: #f0e6d2;
            --text-muted: #a09b8c;
            --border: #3c3c41;
        }

        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: var(--primary);
            color: var(--text);
            line-height: 1.6;
            padding: 20px;
        }

        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }

        header {
            text-align: center;
            margin-bottom: 30px;
            padding: 20px;
            background-color: var(--secondary);
            border-radius: 10px;
            border-bottom: 3px solid var(--accent);
        }

        header h1 {
            color: var(--accent);
            font-size: 2.5rem;
            margin-bottom: 10px;
        }

        .match-container {
            background-color: var(--secondary);
            border-radius: 10px;
            margin-bottom: 30px;
            overflow: hidden;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            border: 1px solid var(--border);
        }

        .match-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 15px 20px;
            background-color: rgba(10, 20, 40, 0.8);
            border-bottom: 1px solid var(--border);
        }

        .match-header h3 {
            color: var(--accent);
            font-weight: 600;
        }

        table {
            width: 100%;
            border-collapse: collapse;
            font-size: 0.95rem;
            table-layout: fixed; /* 고정 테이블 레이아웃 */
        }

        th {
            background-color: rgba(12, 22, 42, 0.7);
            color: var(--accent);
            text-align: left;
            padding: 12px 15px;
            font-weight: 600;
        }

        td {
            padding: 12px 15px;
            border-bottom: 1px solid var(--border);
            white-space: nowrap; /* 텍스트 줄바꿈 방지 */
            overflow: hidden; /* 넘치는 내용 숨김 */
            text-overflow: ellipsis; /* 넘치는 텍스트에 ... 표시 */
        }

        /* 컬럼 너비 고정 */
        th:nth-child(1), td:nth-child(1) { width: 20%; } /* 소환사 */
        th:nth-child(2), td:nth-child(2) { width: 20%; } /* 챔피언 */
        th:nth-child(3), td:nth-child(3) { width: 20%; } /* KDA */
        th:nth-child(4), td:nth-child(4) { width: 10%; } /* 승리 */
        th:nth-child(5), td:nth-child(5) { width: 15%; } /* 골드 */
        th:nth-child(6), td:nth-child(6) { width: 15%; } /* 피해량 */

        tr:last-child td {
            border-bottom: none;
        }

        tr.win {
            background-color: rgba(40, 167, 69, 0.1);
            border-left: 4px solid var(--win);
        }

        tr.lose {
            background-color: rgba(220, 53, 69, 0.1);
            border-left: 4px solid var(--lose);
        }

        .champion-cell {
            display: flex;
            align-items: center;
            gap: 10px;
            min-width: 0; /* 플렉스 아이템이 너무 작아지지 않도록 함 */
            overflow: visible; /* 이미지가 잘리지 않도록 함 */
            white-space: nowrap;
        }

        .champion-img-container {
            flex: 0 0 40px; /* 이미지 컨테이너 크기 고정 */
            height: 40px;
            min-width: 40px; /* 최소 너비 설정 */
        }

        .champion-img {
            width: 40px;
            height: 40px;
            border-radius: 50%;
            border: 2px solid var(--accent);
            display: block; /* 인라인 요소의 여백 문제 해결 */
        }

        .champion-name {
            flex: 1;
            overflow: hidden;
            text-overflow: ellipsis;
            min-width: 0; /* 필요한 경우 0까지 줄어들 수 있도록 함 */
        }

        .summoner-name {
            color: var(--text);
            text-decoration: none;
            display: inline-block;
            position: relative;
            font-weight: 500;
            transition: all 0.2s;
            max-width: 100%;
            overflow: hidden;
            text-overflow: ellipsis;
        }

        .summoner-name::after {
            content: '';
            position: absolute;
            width: 0;
            height: 2px;
            bottom: -2px;
            left: 0;
            background-color: var(--accent);
            transition: width 0.3s;
        }

        .summoner-name:hover {
            color: var(--accent);
        }

        .summoner-name:hover::after {
            width: 100%;
        }

        .kda {
            font-weight: 600;
        }

        .win-icon {
            color: var(--win);
            font-weight: bold;
            text-align: left;
        }

        .lose-icon {
            color: var(--lose);
            font-weight: bold;
            text-align: left;
        }

        .stats {
            color: var(--text-muted);
        }

        .no-matches {
            text-align: center;
            padding: 40px;
            background-color: var(--secondary);
            border-radius: 10px;
            margin-bottom: 30px;
        }

        .btn {
            display: inline-block;
            background-color: var(--accent);
            color: var(--primary);
            padding: 12px 25px;
            border: none;
            border-radius: 5px;
            cursor: pointer;
            text-decoration: none;
            font-weight: 700;
            transition: all 0.3s;
            margin-top: 20px;
        }

        .btn:hover {
            background-color: #d5a84a;
            transform: translateY(-2px);
            box-shadow: 0 4px 12px rgba(200, 155, 60, 0.3);
        }

        .btn i {
            margin-right: 8px;
        }

        .text-center {
            text-align: center;
        }

        @media (max-width: 768px) {
            table {
                font-size: 0.8rem;
            }

            th, td {
                padding: 8px 10px;
            }

            .champion-img-container {
                flex: 0 0 30px;
                height: 30px;
                min-width: 30px;
            }

            .champion-img {
                width: 30px;
                height: 30px;
            }

            .hide-mobile {
                display: none;
            }

            /* 모바일에서 컬럼 너비 조정 */
            th:nth-child(1), td:nth-child(1) { width: 35%; }
            th:nth-child(2), td:nth-child(2) { width: 30%; }
            th:nth-child(3), td:nth-child(3) { width: 25%; }
            th:nth-child(4), td:nth-child(4) { width: 10%; }
        }

        .rank-container {
            background-color: var(--secondary);
            border-radius: 10px;
            margin-bottom: 30px;
            padding: 20px;
            box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
            border: 1px solid var(--border);
        }

        .rank-title {
            color: var(--accent);
            margin-bottom: 15px;
            font-size: 1.5rem;
            border-bottom: 1px solid var(--border);
            padding-bottom: 10px;
        }

        .rank-list {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
        }

        .rank-card {
            display: flex;
            align-items: center;
            flex: 1;
            min-width: 300px;
            background-color: rgba(10, 20, 40, 0.5);
            border-radius: 8px;
            padding: 15px;
            border-left: 4px solid var(--accent);
        }

        .rank-icon {
            flex: 0 0 64px;
            margin-right: 15px;
        }

        .tier-img {
            width: 64px;
            height: 64px;
        }

        .rank-info {
            flex: 1;
        }

        .queue-type {
            font-size: 1rem;
            color: var(--text-muted);
            margin-bottom: 5px;
        }

        .tier-rank {
            font-size: 1.5rem;
            font-weight: bold;
            color: var(--accent);
            margin-bottom: 5px;
        }

        .league-points {
            font-size: 1.1rem;
            margin-bottom: 5px;
        }

        .win-loss {
            font-size: 0.9rem;
            color: var(--text-muted);
        }

        .wins {
            color: var(--win);
            font-weight: bold;
        }

        .losses {
            color: var(--lose);
            font-weight: bold;
        }

        .win-rate {
            color: #FFD700;
            margin-left: 5px;
        }

        @media (max-width: 768px) {
            .rank-card {
                min-width: 100%;
            }

            .tier-img {
                width: 48px;
                height: 48px;
            }

            .rank-icon {
                flex: 0 0 48px;
            }
        }
    </style>
</head>
<body>
<div class="container">
    <header>
        <h1><i class="fas fa-trophy"></i> LoL 전적 검색</h1>
        <p>소환사의 최근 전적 정보를 확인하세요</p>
    </header>

    <%
        List<Map<String, Object>> rankList = (List<Map<String, Object>>) request.getAttribute("rankData");

        // 랭크 데이터가 있는 경우 출력
        if (rankList != null && !rankList.isEmpty()) {
    %>
    <div class="rank-container">
        <h2 class="rank-title"><i class="fas fa-trophy"></i> 소환사 랭크 정보</h2>
        <div class="rank-list">
            <% for(Map<String, Object> rank : rankList) {
                String tier = (String)rank.get("tier");
                String rankValue = (String)rank.get("rank");
                String queueType = (String)rank.get("queueType");
                int wins = (int)rank.get("wins");
                int losses = (int)rank.get("losses");
                int leaguePoints = (int)rank.get("leaguePoints");
                int totalGames = wins + losses;
                double winRate = totalGames > 0 ? (double)wins / totalGames * 100 : 0;
            %>
            <div class="rank-card">
                <div class="rank-icon">
                    <img src="https://ndkoonhkiadlqwhqxlsp.supabase.co/storage/v1/object/public/tier/<%= tier.toLowerCase() %>.png" alt="<%= tier %>" class="tier-img">
                </div>
                <div class="rank-info">
                    <h3 class="queue-type"><%= queueType %></h3>
                    <div class="tier-rank"><%= tier %> <%= rankValue %></div>
                    <div class="league-points"><%= leaguePoints %> LP</div>
                    <div class="win-loss">
                        <span class="wins"><%= wins %>승</span> <span class="losses"><%= losses %>패</span>
                        <span class="win-rate">(<%= String.format("%.1f", winRate) %>%)</span>
                    </div>
                </div>
            </div>
            <% } %>
        </div>
    </div>
    <% } %>
    <%
        List<List<Map<String, Object>>> matches = (List<List<Map<String, Object>>>) request.getAttribute("matches");
        if (matches != null && !matches.isEmpty()) {
            int matchNum = 1;
            for (List<Map<String, Object>> match : matches) {
                // Determine if match has any winners to display match result
                boolean blueTeamWin = false;
                String queueType = "";

                for (Map<String, Object> player : match) {
                    queueType = (String) player.get("queueType");
                    if ((boolean) player.get("win")) {
                        blueTeamWin = true;
                        break;
                    }
                    else{
                        blueTeamWin = false;
                        break;
                    }
                }
    %>
    <div class="match-container">
        <div class="match-header">
            <h3>게임 #<%= matchNum++ %></h3>
            <span><%= queueType%></span>
            <span><%= blueTeamWin ? "블루팀 승리" : "레드팀 승리" %></span>
        </div>
        <table>
            <thead>
            <tr>
                <th>소환사</th>
                <th>챔피언</th>
                <th>KDA</th>
                <th>승리</th>
                <th class="hide-mobile">골드</th>
                <th class="hide-mobile">피해량</th>
            </tr>
            </thead>
            <tbody>
            <%
                for (Map<String, Object> player : match) {
                    boolean win = (boolean) player.get("win");
                    int kills = (int) player.get("kills");
                    int deaths = (int) player.get("deaths");
                    int assists = (int) player.get("assists");

                    // Calculate KDA ratio
                    float kda = (deaths == 0) ? (kills + assists) : (float)(kills + assists) / deaths;
            %>
            <tr class="<%= win ? "win" : "lose" %>">
                <td>
                    <a class="summoner-name" href="<%= request.getContextPath()%>/answer?summonerName=<%=player.get("summoner")%>%23<%= player.get("summonertag")%>" title="<%= player.get("summoner")%>#<%= player.get("summonertag")%>">
                        <%= player.get("summoner")%>#<%= player.get("summonertag")%>
                    </a>
                </td>
                <td>
                    <div class="champion-cell">
                        <div class="champion-img-container">
                            <img class="champion-img" src="https://ddragon.leagueoflegends.com/cdn/15.5.1/img/champion/<%=player.get("champ")%>.png" alt="<%= player.get("champ") %>">
                        </div>
                        <div class="champion-name" title="<%= player.get("champ") %>">
                            <%= player.get("champ") %>
                        </div>
                    </div>
                </td>
                <td class="kda">
                    <span style="color: #28a745;"><%= kills %></span> /
                    <span style="color: #dc3545;"><%= deaths %></span> /
                    <span style="color: #17a2b8;"><%= assists %></span>
                    <div style="font-size: 0.8rem; color: <%= kda >= 3 ? "#FFD700" : (kda >= 2 ? "#C0C0C0" : "#CD7F32") %>;">
                        <%= String.format("%.2f", kda) %> KDA
                    </div>
                </td>
                <td class="<%= win ? "win-icon" : "lose-icon" %>">
                    <%= win ? "<i class=\"fas fa-check-circle\"></i>" : "<i class=\"fas fa-times-circle\"></i>" %>
                </td>
                <td class="stats hide-mobile"><i class="fas fa-coins"></i> <%= String.format("%,d", player.get("gold")) %></td>
                <td class="stats hide-mobile"><i class="fas fa-bolt"></i> <%= String.format("%,d", player.get("damage")) %></td>
            </tr>
            <%
                }
            %>
            </tbody>
        </table>
    </div>
    <%
        }
    } else {
    %>
    <div class="no-matches">
        <i class="fas fa-search" style="font-size: 3rem; color: var(--text-muted); margin-bottom: 20px;"></i>
        <h2>전적을 찾을 수 없습니다</h2>
        <p>소환사 이름을 다시 확인해 주세요.</p>
    </div>
    <%
        }
    %>

    <div class="text-center">
        <a href="/" class="btn">
            <i class="fas fa-redo"></i> 다시 검색하기
        </a>
    </div>
</div>
<script>
    function toggleMatch(element) {
        // Get the next sibling (match details)
        const matchDetails = element.nextElementSibling;

        // Toggle display
        if (matchDetails.style.display === 'block') {
            matchDetails.style.display = 'none';
        } else {
            matchDetails.style.display = 'block';
        }
    }
</script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/js/all.min.js"></script>
</body>
</html>