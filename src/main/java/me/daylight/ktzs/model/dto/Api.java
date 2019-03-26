package me.daylight.ktzs.model.dto;


import java.util.List;

/**
 * @author Daylight
 * @date 2019/01/31 14:09
 */
public class Api {
    private String url;

    private String description;

    private String params;

    private String type;

    private List<String> role;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<String> getRole() {
        return role;
    }

    public void setRole(List<String> role) {
        this.role = role;
    }
}
