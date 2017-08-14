package com.tianbin.youdaowordbookspider;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.tianbin.youdaospider.YouDaoSpider;
import com.tianbin.youdaospider.model.Wordbook;

import java.io.IOException;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * WordBookActivity
 * Created by tianbin on 2017/8/13.
 */
public class WordBookActivity extends AppCompatActivity {

    private static final String COOKIE = "cookie";

    private String TAG = WordBookActivity.class.getName();

    public static void start(Context context, String cookie) {
        final Intent intent = new Intent(context, WordBookActivity.class);
        intent.putExtra(COOKIE, cookie);
        context.startActivity(intent);
    }

    private TextView mTvWordNum;
    private RecyclerView mRecyclerView;
    private WordbookAdapter mWordbookAdapter;

    private YouDaoSpider mYouDaoSpider;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_word_book);

        mYouDaoSpider = new YouDaoSpider();

        mTvWordNum = (TextView) findViewById(R.id.tv_word_num);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = dp2px(8);
            }
        });

        mWordbookAdapter = new WordbookAdapter();
        mRecyclerView.setAdapter(mWordbookAdapter);

        fetchWordbook();
    }

    private void fetchWordbook() {
        Observable.just(null)
                .map(new Func1<Object, Wordbook>() {
                    @Override
                    public Wordbook call(Object o) {
                        try {
                            return mYouDaoSpider.fetchWords(getCookie());
                        } catch (IOException e) {
                            e.printStackTrace();
                            return null;
                        }
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Wordbook>() {
                    @Override
                    public void call(Wordbook wordbook) {
                        if (wordbook != null && wordbook.wordList != null && !wordbook.wordList.isEmpty()) {
                            mTvWordNum.setText(getString(R.string.word_count, wordbook.wordCount));
                            mWordbookAdapter.setNewData(wordbook.wordList);
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        Log.e(TAG, throwable.toString());
                    }
                });
    }

    private String getCookie() {
        return getIntent().getStringExtra(COOKIE);
    }

    public int dp2px(float dpValue) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
    }
}
