package com.tianbin.youdaowordbookspider;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.tianbin.youdaospider.YouDaoSpider;

import java.security.MessageDigest;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final YouDaoSpider youDaoSpider = new YouDaoSpider();

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String userName = getUserName();
                final String password = getPassword();

                Observable.just(null)
                        .map(new Func1<Object, String>() {
                            @Override
                            public String call(Object o) {
                                try {
                                    return youDaoSpider.fetchCookie(userName, getMD5(password));
                                } catch (Exception e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<String>() {
                            @Override
                            public void call(String cookie) {
                                Log.d(TAG, cookie);

                                if (!TextUtils.isEmpty(cookie)) {
                                    WordBookActivity.start(MainActivity.this, cookie);
                                }
                            }
                        }, new Action1<Throwable>() {
                            @Override
                            public void call(Throwable throwable) {
                                Log.e(TAG, throwable.toString());
                            }
                        });

            }
        });

    }

    private String getUserName() {
        EditText etUserName = (EditText) findViewById(R.id.et_user_name);
        return etUserName.getText().toString();
    }

    private String getPassword() {
        EditText etPassword = (EditText) findViewById(R.id.et_password);
        return etPassword.getText().toString();
    }

    private String getMD5(String str) throws Exception {
        byte[] hash;
        hash = MessageDigest.getInstance("MD5").digest(str.getBytes("UTF-8"));
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10) hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}
