<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>롤 전적 검색</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #0A1428;
            color: #F0E6D2;
            margin: 0;
            padding: 0;
            background-position: center;
            background-attachment: fixed;
        }

        .container {
            max-width: 600px;
            margin: 50px auto;
            padding: 30px;
            background-color: rgba(9, 20, 40, 0.85);
            border-radius: 8px;
            box-shadow: 0 0 20px rgba(0, 0, 0, 0.5);
            border: 1px solid #C8AA6E;
        }

        h2 {
            text-align: center;
            color: #C8AA6E;
            font-size: 28px;
            margin-bottom: 30px;
            text-transform: uppercase;
            letter-spacing: 2px;
            text-shadow: 0 0 5px #C8AA6E;
        }

        form {
            display: flex;
            flex-direction: column;
            align-items: center;
        }

        label {
            font-size: 16px;
            margin-bottom: 8px;
            color: #C8AA6E;
        }

        input[type="text"] {
            width: 100%;
            padding: 12px;
            margin-bottom: 20px;
            background-color: #1E2328;
            border: 2px solid #785A28;
            border-radius: 4px;
            color: #F0E6D2;
            font-size: 16px;
            box-sizing: border-box;
            transition: border-color 0.3s;
        }

        input[type="text"]:focus {
            outline: none;
            border-color: #C8AA6E;
            box-shadow: 0 0 8px rgba(200, 170, 110, 0.5);
        }

        button {
            background-color: #1E2328;
            color: #C8AA6E;
            border: 2px solid #C8AA6E;
            border-radius: 4px;
            padding: 12px 24px;
            font-size: 16px;
            cursor: pointer;
            transition: all 0.3s ease;
            text-transform: uppercase;
            letter-spacing: 1px;
            font-weight: bold;
        }

        button:hover {
            background-color: #C8AA6E;
            color: #0A1428;
            box-shadow: 0 0 10px #C8AA6E;
        }

        .logo {
            display: flex;
            justify-content: center;
            align-items: center;
            margin-bottom: 20px;
        }

        .logo img {
            max-width: 120px;
            height: auto;
        }

        footer {
            text-align: center;
            margin-top: 30px;
            font-size: 12px;
            color: #5B5A56;
        }
    </style>
</head>
<body>
<div class="container">
    <div class="logo">
        <a href= "/">
            <img src="https://ndkoonhkiadlqwhqxlsp.supabase.co/storage/v1/object/public/logo//lollogo.png" alt="LoL Logo">
        </a>
        <a href= "/tft">
            <img src="https://ndkoonhkiadlqwhqxlsp.supabase.co/storage/v1/object/public/logo//tftlogo.png" alt="LoL Logo">
        </a>
    </div>
    <h2><%= session.getAttribute("gameMode") %> 전적검색</h2>
    <form method="post">
        <label for="summonerName">소환사 이름:</label>
        <input type="text" id="summonerName" name="summonerName" placeholder="소환사 이름을 입력하세요" required>
        <% if(session.getAttribute("error") != null) {%>
            <%=session.getAttribute("error")%>
        <% } %>
        <button type="submit">검색</button>
    </form>
    <footer>
        본 서비스는 Riot Games의 공식 사이트가 아니며, Riot Games의 API를 활용한 비공식 서비스입니다.
    </footer>
</div>
</body>
</html>