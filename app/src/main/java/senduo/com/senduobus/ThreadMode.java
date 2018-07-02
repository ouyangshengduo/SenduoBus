package senduo.com.senduobus;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/27
 * * 文件描述：
 * * 修改历史：2018/6/27 14:43*************************************
 **/
public enum  ThreadMode {

    /**
     * Subscriber will be called in the same thread, which is posting the event. This is the default. Event delivery
     * implies the least overhead because it avoids thread switching completely. Thus this is the recommended mode for
     * simple tasks that are known to complete is a very short time without requiring the main thread. Event handlers
     * using this mode must return quickly to avoid blocking the posting thread, which may be the main thread.
     * 如果使用事件处理函数指定了线程模型为PostThread，
     * 那么该事件在哪个线程发布出来的，
     * 事件处理函数就会在这个线程中运行，
     * 也就是说发布事件和接收事件在同一个线程。
     * 在线程模型为PostThread的事件处理函数中尽量避免执行耗时操作，
     * 因为它会阻塞事件的传递，甚至有可能会引起ANR。
     */
    PostThread,

    /**
     * Subscriber will be called in Android's main thread (sometimes referred to as UI thread). If the posting thread is
     * the main thread, event handler methods will be called directly. Event handlers using this mode must return
     * quickly to avoid blocking the main thread.
     * 如果使用事件处理函数指定了线程模型为MainThread，
     * 那么不论事件是在哪个线程中发布出来的，
     * 该事件处理函数都会在UI线程中执行。
     * 该方法可以用来更新UI，
     * 但是不能处理耗时操作。
     */
    MainThread,

    /**
     * Subscriber will be called in a background thread. If posting thread is not the main thread, event handler methods
     * will be called directly in the posting thread. If the posting thread is the main thread, EventBus uses a single
     * background thread, that will deliver all its events sequentially. Event handlers using this mode should try to
     * return quickly to avoid blocking the background thread.
     * 如果使用事件处理函数指定了线程模型为BackgroundThread，
     * 那么如果事件是在UI线程中发布出来的，
     * 那么该事件处理函数就会在新的线程中运行，
     * 如果事件本来就是子线程中发布出来的，
     * 那么该事件处理函数直接在发布事件的线程中执行。
     * 在此事件处理函数中禁止进行UI更新操作。
     */
    BackgroundThread,

    /**
     * Event handler methods are called in a separate thread. This is always independent from the posting thread and the
     * main thread. Posting events never wait for event handler methods using this mode. Event handler methods should
     * use this mode if their execution might take some time, e.g. for network access. Avoid triggering a large number
     * of long running asynchronous handler methods at the same time to limit the number of concurrent threads. EventBus
     * uses a thread pool to efficiently reuse threads from completed asynchronous event handler notifications.
     * 如果使用事件处理函数指定了线程模型为Async，
     * 那么无论事件在哪个线程发布，
     * 该事件处理函数都会在新建的子线程中执行。
     * 同样，此事件处理函数中禁止进行UI更新操作
     */
    Async

}
