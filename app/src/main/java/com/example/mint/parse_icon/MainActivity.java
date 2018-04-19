package com.example.mint.parse_icon;

import android.app.ProgressDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements MainContract.IMainView, View.OnClickListener {
    private EditText editUrl;
    private TextView btnParse;
    private ImageView imageIcon;

    private ProgressDialog progressDialog;
    private ParsePresenter parsePresenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editUrl = findViewById(R.id.edit_url);
        btnParse = findViewById(R.id.btn_parse);
        btnParse.setOnClickListener(this);
        imageIcon = findViewById(R.id.image_icon);

        parsePresenter = new ParsePresenter(this);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        parsePresenter.unsubscribe();
        EventBus.getDefault().unregister(this);
    }

    public static class MessageEvent {
        public final String message;

        public MessageEvent(String message) {
            this.message = message;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        Toast.makeText(this, event.message, Toast.LENGTH_SHORT).show();
    }

    @Subscribe
    public void handleSomethingElse(Object event){
        Toast.makeText(this, event.toString(), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_parse:
                Pattern pattern = Patterns.WEB_URL;
                final String url = editUrl.getText().toString();
                if (TextUtils.isEmpty(url) || !pattern.matcher(url).matches()) {
                    Toast.makeText(MainActivity.this, "请输入有效网址", Toast.LENGTH_SHORT).show();
                } else {
                    parsePresenter.parseIcon(url);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void showProgress() {
        if(progressDialog == null){
            progressDialog = new ProgressDialog(this);
        }
        progressDialog.show();
    }

    @Override
    public void hideProgress() {
        if(progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onParseSuccess(String url) {
        Toast.makeText(this,url,Toast.LENGTH_SHORT).show();
        if(!TextUtils.isEmpty(url)){
            Picasso.get().load(url).into(imageIcon);
        }
    }

    @Override
    public void onParseError(Throwable t) {
        Toast.makeText(this,t.getMessage(),Toast.LENGTH_SHORT).show();
    }
}
