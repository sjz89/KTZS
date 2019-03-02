package me.daylight.ktzs.model.dto;

import java.io.Serializable;

/**
 * @author Daylight
 * @date 2019/02/15 17:38
 */
public class HttpRecord implements Serializable {
    private String URL;

    private String METHOD;

    private String IP;

    private String SESSION;

    private String USER;

    private String CLASS_METHOD;

    private String ARGS;

    private String PARAMS;
    
    private String RESPONSE;
    
    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getMETHOD() {
        return METHOD;
    }

    public void setMETHOD(String METHOD) {
        this.METHOD = METHOD;
    }

    public String getIP() {
        return IP;
    }

    public void setIP(String IP) {
        this.IP = IP;
    }

    public String getSESSION() {
        return SESSION;
    }

    public void setSESSION(String SESSION) {
        this.SESSION = SESSION;
    }

    public String getUSER() {
        return USER;
    }

    public void setUSER(String USER) {
        this.USER = USER;
    }

    public String getCLASS_METHOD() {
        return CLASS_METHOD;
    }

    public void setCLASS_METHOD(String CLASS_METHOD) {
        this.CLASS_METHOD = CLASS_METHOD;
    }

    public String getARGS() {
        return ARGS;
    }

    public void setARGS(String ARGS) {
        this.ARGS = ARGS;
    }

    public String getRESPONSE() {
        return RESPONSE;
    }

    public void setRESPONSE(String RESPONSE) {
        this.RESPONSE = RESPONSE;
    }

    public String getPARAMS() {
        return PARAMS;
    }

    public void setPARAMS(String PARAMS) {
        this.PARAMS = PARAMS;
    }
}
