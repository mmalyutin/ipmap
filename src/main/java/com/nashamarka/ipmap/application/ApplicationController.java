/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.nashamarka.ipmap.application;

import com.nashamarka.ipmap.entities.JsonOject;
import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.model.CityResponse;
import com.maxmind.geoip2.record.City;
import com.maxmind.geoip2.record.Country;
import com.maxmind.geoip2.record.Location;
import com.maxmind.geoip2.record.Postal;
import com.maxmind.geoip2.record.Subdivision;
import java.net.InetAddress;
import java.util.LinkedList;

/**
 *
 * @author Вадик
 */
@Named
@ApplicationScoped

public class ApplicationController implements Serializable {

    public ApplicationController() {
    }
    //File dbFile = new File("F:\\lib\\geoip\\lib\\GeoLite2-City.mmdb");
    File dbFile = new File("/home/vadik/GeoLite2-City.mmdb");
    DatabaseReader reader = null;
    private ArrayList<String> iplist = new ArrayList<String>();
    private ArrayList<JsonOject> jstemp = new ArrayList<JsonOject>();
    private ArrayList<JsonOject> jsreal = new ArrayList<JsonOject>();
    private HashMap<String, JsonOject> alljs = new HashMap();
    private HashMap session = new HashMap();
    private LinkedList<String> strIp = new LinkedList<String>();
    int id;
    int current;
    Date datelast = new Date();

    public ArrayList<String> getIplist() {
        return iplist;
    }

    public HashMap getSession() {
        return session;
    }

    public int getCurrentUsers() {
        return current;
    }

    public HashMap<String, JsonOject> getAlljs() {
        return alljs;
    }

    //Current ip
    public JsonOject getJs(String ip) {
        JsonOject js = alljs.get(ip);
        return js;
    }

    //Change ip
    public JsonOject getJs(String id, String ip) {
        String removeip = (String) session.get(id);
        remSession(id, removeip);
        putSession(id, ip);
        putSession(ip);
        return alljs.get(ip);
    }

    private JsonOject param(String ip) {
        JsonOject js;
        js = alljs.get(ip);
        if (js != null) {
            return js;
        }
        try {
            if (reader == null) {
                reader = new DatabaseReader.Builder(dbFile).build();
            }
            js = new JsonOject();
            InetAddress ipAddress = InetAddress.getByName(ip);
            CityResponse response = reader.city(ipAddress);
            Location location = response.getLocation();
            if (id > 100000) {
                id = 0;
            }
            js.setId(++id);
            js.setLat(location.getLatitude().toString());
            js.setLon(location.getLongitude().toString());
            js.setIp(ip);
            Country country = response.getCountry();
            js.setCountry(country.getName());
            Subdivision subdivision = response.getMostSpecificSubdivision();
            js.setSubdivision(subdivision.getName());
            City city = response.getCity();
            js.setCity(city.getName());
            Postal postal = response.getPostal();
            js.setPostal(postal.getCode());
            return js;
        } catch (Exception e) {
            System.out.println("error in dbFile or in ip " + ip);
            //e.printStackTrace();
            return null;
        }

    }

    public synchronized void putSession(String id, String ip) {
        session.put(id, ip);
    }

    public void putSession(String ip) {
        JsonOject js = param(ip);
        if (js != null) {
            synchronized (this) {
                jstemp.add(js);
                iplist.add(ip);
                if (!alljs.containsKey(ip)) {
                    alljs.put(ip, js);
                    strIp.add(ip);
                }
            }
        } else {
            js = new JsonOject();
            js.setId(++this.id);
            js.setLat("55.75");
            js.setLon("37.62");
            js.setCountry("unknown");
            js.setSubdivision("unknown");
            js.setCity("unknown");
            js.setPostal("unknown");
            js.setIp(ip);
            synchronized (this) {
                jstemp.add(js);
                iplist.add(ip);
                alljs.put(ip, js);
                strIp.add(ip);
            }
        }
    }

    public void remSession(String id, String ip) {
        synchronized (this) {
            session.remove(id);
        }
        if (ip == null) {
            return;
        }
        if (!session.containsValue(ip)) {
            for (int i = 0; i < jstemp.size(); i++) {
                if (ip.equals(jstemp.get(i).getIp())) {
                    synchronized (this) {
                        jstemp.remove(i);
                        iplist.remove(ip);
                    }
                    break;
                }
            }
            while (strIp.size() > 1000) {
                synchronized (this) {
                    alljs.remove(strIp.pollFirst());
                }
            }
        }
    }

    public ArrayList<JsonOject> getJson() {

        if (new Date().getTime() - datelast.getTime() > 10000) {
            jsonDo();
        }
        return jsreal;
    }

    private synchronized void jsonDo() {
        if (jsreal.size() > 0) {
            jsreal.clear();
        }
        current = iplist.size();
        datelast = new Date();
        for (int i = 0; i < jstemp.size(); i++) {
            JsonOject js = new JsonOject();
            js.setId(jstemp.get(i).getId());
            js.setLat(jstemp.get(i).getLat());
            js.setLon(jstemp.get(i).getLon());
            js.setIp(jstemp.get(i).getIp());
            jsreal.add(js);
        }
    }

}
