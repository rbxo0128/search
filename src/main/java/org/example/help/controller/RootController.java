package org.example.help.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;


@WebServlet({"/", "/tft"})
public class RootController extends Controller {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html; charset=UTF-8");

        String servletPath = req.getServletPath();

        HttpSession session = req.getSession();
        if (servletPath.equals("/tft")) {
            session.setAttribute("gameMode", "롤체");
        }
        else{
            session.setAttribute("gameMode", "롤");
        }


        view(req, resp, "index");

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setCharacterEncoding("UTF-8");

        String servletPath = req.getServletPath();

        String summonerName = req.getParameter("summonerName");
        String encodedName = URLEncoder.encode(summonerName, StandardCharsets.UTF_8);

        if (servletPath.equals("/tft")) {
            resp.sendRedirect(req.getContextPath() + "/tft/answer?summonerName=" + encodedName);
        }
        else{
            resp.sendRedirect(req.getContextPath() + "/answer?summonerName=" + encodedName);
        }

    }
}
