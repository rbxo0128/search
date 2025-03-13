<%@ page import="java.util.Map" %>
<%@ page import="java.util.List" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <meta charset="UTF-8">
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

        #champions{
            display: flex;
            flex-direction: row;
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
            th:nth-child(1), td:nth-child(1) { width: 30%; }
            th:nth-child(2), td:nth-child(2) { width: 10%; }
            th:nth-child(3), td:nth-child(3) { width: 10%; }
            th:nth-child(4), td:nth-child(4) { width: 50%; }
        }
    </style>
</head>
<body>
<div class="container">
    <header>
        <h1><i class="fas fa-trophy"></i> TFT 전적 검색</h1>
        <p>소환사의 최근 TFT 전적 정보를 확인하세요</p>
    </header>

    <%
        List<List<Map<String, Object>>> matches = (List<List<Map<String, Object>>>) request.getAttribute("matches");
        if (matches != null && !matches.isEmpty()) {
            int matchNum = 1;
            for (List<Map<String, Object>> match : matches) {
                // 각 경기의 공통 정보는 첫 플레이어 데이터에 저장되어 있음.
                long gameDatetime = 0;
                int gameLength = 0;
                String queueType = "";
                if (!match.isEmpty()) {
                    Map<String, Object> firstPlayer = match.get(0);
                    gameDatetime = (Long) firstPlayer.get("gameDatetime");
                    gameLength = (Integer) firstPlayer.get("gameLength");
                    queueType = (String) firstPlayer.get("queueType");
                }
    %>
    <div class="match-container">
        <div class="match-header">
            <h3>게임 #<%= matchNum++ %></h3>
            <div>
                <span><strong>큐 타입:</strong> <%= queueType %></span>
                <span style="margin-left: 15px;"><strong>게임 길이:</strong> <%= gameLength %>초</span>
                <span style="margin-left: 15px;"><strong>시작 시간:</strong> <%= new java.util.Date(gameDatetime) %></span>
            </div>
        </div>
        <table>
            <thead>
            <tr>
                <th style="width: 30%">소환사</th>
                <th style="width: 10%">순위</th>
                <th style="width: 10%">레벨</th>
                <th style="width: 50%">사용한 챔피언</th>
            </tr>
            </thead>
            <tbody>
            <%
                for (Map<String, Object> player : match) {
                    int placement = (Integer) player.get("placement");
                    int level = (Integer) player.get("level");
                    List<String> champions = (List<String>) player.get("champions");
            %>
            <tr>
                <td>
                    <a class="summoner-name" href="<%= request.getContextPath()%>/tft/answer?summonerName=<%= player.get("summoner") %>%23<%= player.get("summonertag") %>"
                       title="<%= player.get("summoner") %>#<%= player.get("summonertag") %>">
                        <%= player.get("summoner") %>#<%= player.get("summonertag") %>
                    </a>
                </td>
                <td><%= placement %> 등</td>
                <td><%= level %></td>
                <td id="champions">
                    <%
                        // 챔피언 이미지 아이콘 표시 (여러 개일 경우 반복)
                        for(String champ : champions) {
                    %>
                    <div class="champion-cell">
                        <img class="champion-img" src="https://ndkoonhkiadlqwhqxlsp.supabase.co/storage/v1/object/public/tft//<%= champ %>.png"
                             alt="<%= champ %>" title="<%= champ %>">
                    </div>
                    <%
                        }
                    %>
                </td>
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
        <i class="fas fa-search" style="font-size: 3rem; color: #999; margin-bottom: 20px;"></i>
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
</body>
</html>
