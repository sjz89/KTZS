package me.daylight.ktzs.model.dto;

import java.util.List;

/**
 * @author Daylight
 * @date 2019/03/09 08:27
 */
public class WsMsg {
    private Object data;

    private List<String> receiverIdList;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public List<String> getReceiverIdList() {
        return receiverIdList;
    }

    public void setReceiverIdList(List<String> receiverIdList) {
        this.receiverIdList = receiverIdList;
    }
}
