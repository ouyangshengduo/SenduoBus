# **开篇废话** #

近期利用业余时间，跟着大神把Eventbus的框架学习了一下，在这里，记录一下这次学习的心得。
EventBus是针一款对Android的发布/订阅事件总线。它可以让我们很轻松的实现在Android各个组件之间传递消息，并且代码的可读性更好，耦合度更低。
这次学习 ，大概有以下这些知识点：

      1.注解和反射的使用
      2.学习Eventbus的实现原理
需要注意的是，本次记录是从实现的角度来进行记录的，可能跟我们调用的逻辑顺序不一样。

----------


# **技术详情** #

## **1. 事件接收者订阅想要的事件** ##
这一步实现的是，告诉事件生产者，我有这些事件需要处理，通过注解进行标记，方便我们的框架进行收集此类需要处理的事件，例如以下这些操作：

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void showTextView(SenduoEvent senduoEvent){
        tvSenduobusInfo.setText(senduoEvent.toString());
    }

    @Subscribe(threadMode = ThreadMode.Async)
    public void showLog(SenduoEvent senduoEvent){
        Log.e(TAG,senduoEvent.toString());
    }


通过以上步骤，就相当于告诉我们的框架，这里需要处理这两个事件


## **2. 往事件生产者注册 和 反注册想要订阅的事件** ##

通过第一步，事件接收者已经把想要接收的事件告知了我们这个框架，我们这个框架，就需要通过不同的类来收集这些事件，调用者，通过调用以下这行代码，框架就会进行收集记录各自订阅的事件：

      Senduobus.getDefault().register(this);

这一步，框架其实做了蛮多工作的。
首先，通过方法的注解，记录当前类订阅的所有事件

    public void register(Object activity){

        List<SubscribleMethod> list = cacheMap.get(activity);

        if(list == null){
            list = getSubscribleMethods(activity);
            cacheMap.put(activity,list);
        }

    }

这里的getSubscribleMethods(Object)方法就是通过类的注解，来获取处理事件的方法：
      
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

然后，通过一个内存缓存Map记录类与对应类的订阅事件，减少框架反射的次数

## **3. 事件生产者分发事件** ##

通过以上两步，事件接收者，以及其订阅的事件都已经收集并且绑定好了，就差事件生产者进行分发消息了。
这里需要注意的是，如何确认当前这个事件需要由哪个接收者来处理，框架中，是通过方法的参数类型进行分发确认的，以下这种形式：

        Senduobus.getDefault().post(new SenduoEvent("1","测试发送消息"));

其中SenduoEvent类，通过这个类来确认该由哪些事件接收者来处理事件：

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


里面有一些线程切换的逻辑，具体可查看ThreadMode中的注释，方便理解。


以下是此项目的简书地址：

[Senduobus](https://www.jianshu.com/p/f26d7506c924)


