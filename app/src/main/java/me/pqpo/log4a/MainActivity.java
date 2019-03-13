package me.pqpo.log4a;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import me.pqpo.librarylog4a.Log4a;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    EditText etContent;
    EditText etThread;
    Button btnWrite;
    Button btnTest;
    TextView tvTest;
    EditText etTimes;

    boolean testing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etThread = findViewById(R.id.et_thread);
        etContent = findViewById(R.id.et_content);
        btnWrite = findViewById(R.id.btn_write);
        btnTest = findViewById(R.id.btn_test);
        tvTest = findViewById(R.id.tv_test);
        etTimes = findViewById(R.id.et_times);


        Log4a.d("MainActivity","Hello world\nLinkZhang");
        Log4a.json("MainActivity","{\"msgBody\":\"1 bar xnxx ki ma night meh..Chh gaye aur dhekha xnxx ki maa ki chut meh tha lund ka b\",\"msgType\":1,\"userInfo\":{\"head\":\"http:\\/\\/download.dual-whatsapp.com\\/5c6acaf24011b00037cff117\\/20190311\\/head_1552311054897.jpg\",\"fromUid\":\"5c6acaf24011b00037cff117\",\"fromGender\":2,\"name\":\"Niki\"},\"timestamp\":1552361548273,\"msgInfo\":{},\"mid\":\"5c87284cd797fc003ab043df\"}");
    }




    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log4a.flush();
    }
}
