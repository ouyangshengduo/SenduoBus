package senduo.com.senduobus;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
    }

    public void postMsg(View view) {
        Senduobus.getDefault().post(new SenduoEvent("1","测试发送消息"));
        finish();
    }
}
