/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nashamarka.ipmap.data;

import com.nashamarka.ipmap.application.ApplicationController;
import com.nashamarka.ipmap.entities.JsonOject;
import java.io.PrintWriter;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
/**
 *
 * @author Вадик
 */
public class Json extends HttpServlet {

    protected static final String MIME_TYPE_JSON = "application/json";
    protected static final String BEGIN = "{\n"
            + "    \"type\": \"FeatureCollection\",\n"
            + "    \"features\": [";
    protected static final String END = "]\n"
            + "}";

    @Inject
    ApplicationController application;

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) {

        try {
            PrintWriter out = null;
            try {
                response.setContentType(MIME_TYPE_JSON);
                response.setCharacterEncoding("utf-8");
                out = response.getWriter();
                out.println(BEGIN);
                List<JsonOject> all = application.getJson();
                for (int i = 0; i < all.size(); i++) {
                    if (i != all.size() - 1) {
                        out.println("{\"type\": \"Feature\", \"id\": " + all.get(i).getId() + ", \"geometry\": {\"type\": \"Point\", \"coordinates\": [" + all.get(i).getLat() + ", " + all.get(i).getLon() + "]}},");
                    } else {
                        out.println("{\"type\": \"Feature\", \"id\": " + all.get(i).getId() + ", \"geometry\": {\"type\": \"Point\", \"coordinates\": [" + all.get(i).getLat() + ", " + all.get(i).getLon() + "]}}");
                    }
                }
                out.println(END);
                out.flush();
                out.close();
                response.flushBuffer();
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }
}
