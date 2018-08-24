/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nashamarka.ipmap.filter;

import com.nashamarka.ipmap.application.ApplicationController;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Вадик
 */
public class SessionCounter implements HttpSessionListener {

    @Inject
    ApplicationController app;

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        HttpServletRequest request = requestHelp();
        if (request == null) {
            return;
        }
        String ipAddress = request.getHeader("X-FORWARDED-FOR");
        if (ipAddress == null) {
            ipAddress = request.getRemoteAddr();
        }
        event.getSession().setMaxInactiveInterval(30);
        if (!app.getSession().containsKey(event.getSession().getId())) {
            app.putSession(event.getSession().getId(), ipAddress);
        }
        if (!app.getIplist().contains(ipAddress)) {
            app.putSession(ipAddress);
        }
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        if (app.getSession().containsKey(event.getSession().getId())) {
            String ipAddress = (String) app.getSession().get(event.getSession().getId());
            app.remSession(event.getSession().getId(), ipAddress);
        }
    }

    public HttpServletRequest requestHelp() {
        try {
            HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            return request;
        } catch (Exception e) {
            return null;

        }

    }
}
