package senduo.com.senduobus;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * *****************************************************************
 * * 文件作者：ouyangshengduo
 * * 创建时间：2018/6/27
 * * 文件描述：
 * * 修改历史：2018/6/27 15:13*************************************
 **/
@Retention(RetentionPolicy.RUNTIME)
public @interface Subscribe {

    ThreadMode threadMode() default ThreadMode.PostThread;
}
