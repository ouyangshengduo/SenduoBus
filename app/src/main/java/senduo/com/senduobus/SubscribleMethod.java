package senduo.com.senduobus;

import java.lang.reflect.Method;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/27
 * * 文件描述：
 * * 修改历史：2018/6/27 14:42*************************************
 **/
public class SubscribleMethod {

    private Method method;

    private ThreadMode threadMode;

    private Class<?> eventType;

    public SubscribleMethod(Method method,ThreadMode threadMode,Class<?> eventType){
        this.method = method;
        this.threadMode = threadMode;
        this.eventType = eventType;
    }

    public Method getMethod() {
        return method;
    }

    public ThreadMode getThreadMode() {
        return threadMode;
    }

    public Class<?> getEventType() {
        return eventType;
    }

}
