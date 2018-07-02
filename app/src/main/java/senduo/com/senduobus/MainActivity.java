package senduo.com.senduobus;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private TextView tvSenduobusInfo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Senduobus.getDefault().register(this);
        tvSenduobusInfo = findViewById(R.id.tv_senduobus_info);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Senduobus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MainThread)
    public void showTextView(SenduoEvent senduoEvent){
        tvSenduobusInfo.setText(senduoEvent.toString());
    }

    @Subscribe(threadMode = ThreadMode.Async)
    public void showLog(SenduoEvent senduoEvent){
        Log.e(TAG,senduoEvent.toString());
    }

    public void skipNextActivity(View view) {
        startActivity(new Intent(this,SecondActivity.class));
    }
}
