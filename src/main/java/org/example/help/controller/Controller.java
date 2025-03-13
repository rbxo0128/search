package org.example.help.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class Controller extends HttpServlet {
    protected void view(HttpServletRequest req, HttpServletResponse resp, String name) throws IOException, ServletException {
        req.getRequestDispatcher("/WEB-INF/views/%s.jsp".formatted(name)).forward(req, resp);
    }
}
