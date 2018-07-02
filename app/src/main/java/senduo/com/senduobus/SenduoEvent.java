package senduo.com.senduobus;

import java.io.Serializable;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/27
 * * 文件描述：
 * * 修改历史：2018/6/27 15:21*************************************
 **/
public class SenduoEvent implements Serializable {
    private String eventType;
    private String eventContent;

    public SenduoEvent(String eventType, String eventContent) {
        this.eventType = eventType;
        this.eventContent = eventContent;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventContent() {
        return eventContent;
    }

    public void setEventContent(String eventContent) {
        this.eventContent = eventContent;
    }

    @Override
    public String toString() {
        return "SenduoEvent{" +
                "eventType='" + eventType + '\'' +
                ", eventContent='" + eventContent + '\'' +
                '}';
    }
}
