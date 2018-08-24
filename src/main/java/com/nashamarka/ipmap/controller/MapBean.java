/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nashamarka.ipmap.controller;

import com.nashamarka.ipmap.application.ApplicationController;
import com.nashamarka.ipmap.entities.JsonOject;
import java.io.Serializable;
import java.util.Date;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Вадик
 */
@ManagedBean(name = "mapBean")
@SessionScoped

public class MapBean implements Serializable {

    public MapBean() {
    }

    private static final long serialVersionUID = 1L;
    private JsonOject js;
    private String mesto = "I am here";
    String ip;

    @Inject
    ApplicationController app;

    private void js() {
        ip = getIp();
        js = app.getJs(ip);
        dateRen = new Date();
        //Change ip
        if (js == null) {
            HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(false);
            js = app.getJs(session.getId(), ip);
        }
        if (js != null && js.getCountry().equals("unknown")) {
            mesto = "Unknown Adress";
        }
    }

    private String getIp() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        ip = request.getHeader("X-FORWARDED-FOR");
        if (ip == null) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }

    Date dateRen = new Date();

    public JsonOject getJs() {
        if (js == null || new Date().getTime() - dateRen.getTime() > 1000) {
            js();
        }
        return js;
    }

    public String getMesto() {
        return mesto;
    }

    public int getCurrent() {
        return app.getCurrentUsers();
    }

}
