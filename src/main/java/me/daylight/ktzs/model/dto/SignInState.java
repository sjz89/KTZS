package me.daylight.ktzs.model.dto;

/**
 * @author Daylight
 * @date 2019/03/09 02:51
 */
public class SignInState {
    private String uniqueId;

    private int count;

    private Long remainTime;

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(Long remainTime) {
        this.remainTime = remainTime;
    }
}
