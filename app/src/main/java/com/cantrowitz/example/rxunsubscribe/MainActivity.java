package com.cantrowitz.example.rxunsubscribe;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.text.DateFormat;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private Subscription subscription;
    private TextView textView;
    private DateFormat df = DateFormat.getDateTimeInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = (TextView) findViewById(android.R.id.text1);

        subscription = NonMainThreadProvider.getTimeObservable()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        textView.setText(df.format(aLong));
                    }
                });

        new Handler().postDelayed(cancelRunnable, 3000);


    }

    private Runnable cancelRunnable = new Runnable() {
        @Override
        public void run() {
            subscription.unsubscribe();
        }
    };
}
