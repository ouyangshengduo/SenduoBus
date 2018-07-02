package senduo.com.senduobus;

import android.os.Handler;
import android.os.Looper;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/27
 * * 文件描述：
 * * 修改历史：2018/6/27 14:39*************************************
 **/
public class Senduobus {
    private static final Senduobus ourInstance = new Senduobus();

    private Map<Object,List<SubscribleMethod>>  cacheMap;

    private ExecutorService executorService;
    private Handler handler;
    public static Senduobus getDefault() {
        return ourInstance;
    }

    private Senduobus() {
        this.cacheMap = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
        handler = new Handler(Looper.getMainLooper());

    }

    public void register(Object activity){

        List<SubscribleMethod> list = cacheMap.get(activity);

        if(list == null){
            list = getSubscribleMethods(activity);
            cacheMap.put(activity,list);
        }

    }

    public void unregister(Object activity){

        if(null != cacheMap){
            cacheMap.remove(activity);
        }
    }

    public void post(final Object senduoEvent){

        Set<Object> set = cacheMap.keySet();

        Iterator iterator = set.iterator();
        while(iterator.hasNext()){
            final Object activity = iterator.next();
            List<SubscribleMethod> list = cacheMap.get(activity);
            for(final SubscribleMethod subscribleMethod : list){
                if(subscribleMethod.getEventType().isAssignableFrom(senduoEvent.getClass())){


                    switch(subscribleMethod.getThreadMode()){
                        case Async:
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                executorService.execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod,activity,senduoEvent);
                                    }
                                });
                            }else{
                                invoke(subscribleMethod,activity,senduoEvent);
                            }
                            break;
                        case PostThread:

                            break;
                        case MainThread:
                            if(Looper.myLooper() == Looper.getMainLooper()){
                                invoke(subscribleMethod,activity,senduoEvent);
                            }else{
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        invoke(subscribleMethod,activity,senduoEvent);
                                    }
                                });
                            }
                            break;
                        case BackgroundThread:
                            break;
                        default:
                            break;
                    }
                }
            }

        }

    }

    private void invoke(SubscribleMethod subscribleMethod, Object activity, Object senduoEvent) {

        Method method = subscribleMethod.getMethod();

        try {
            method.invoke(activity,senduoEvent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private List<SubscribleMethod> getSubscribleMethods(Object activity) {

        List<SubscribleMethod> list = new ArrayList<>();

        Class clazz = activity.getClass();

        while(clazz != null){
            String name = clazz.getName();
            if(name.startsWith("java.")
                    || name.startsWith("javax.")
                    || name.startsWith("android.")){//如果类全名以这些字符开头，则认为是jdk的，不是我们自定义的，自然没必要去拿注解
                break;
            }

            Method[] methods = clazz.getDeclaredMethods();//获得当前class所有生命的public方法
            for(Method method : methods){
                Subscribe subscribe = method.getAnnotation(Subscribe.class);

                if(subscribe == null){
                    continue;
                }

                Class[] paratems = method.getParameterTypes();
                if(paratems.length != 1){
                    throw new RuntimeException("senduobus 只能接收到一个参数");
                }

                ThreadMode threadMode = subscribe.threadMode();

                SubscribleMethod subscribleMethod = new SubscribleMethod(method,threadMode,paratems[0]);
                list.add(subscribleMethod);
            }

            clazz = clazz.getSuperclass();

        }
        return list;
    }
}
