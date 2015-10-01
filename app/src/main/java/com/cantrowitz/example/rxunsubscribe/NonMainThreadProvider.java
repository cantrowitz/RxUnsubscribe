package com.cantrowitz.example.rxunsubscribe;

import android.os.Looper;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action0;
import rx.subscriptions.Subscriptions;

/**
 * Created by adamcantrowitz on 9/30/15.
 */
public class NonMainThreadProvider implements Observable.OnSubscribe<Long> {

    private NonMainThreadProvider() {
    }

    public static Observable<Long> getTimeObservable() {
        return Observable.create(new NonMainThreadProvider());
    }

    @Override
    public void call(Subscriber<? super Long> subscriber) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            throw new IllegalStateException("Not allowed to subscribe on the MainThread");
        }

        subscriber.add(Subscriptions.create(new Action0() {
            @Override
            public void call() {
                if (Looper.getMainLooper() == Looper.myLooper()) {
                    throw new IllegalStateException("Not allowed to unsubscribe on the MainThread");
                }
            }
        }));

        while (true) {
            if (!subscriber.isUnsubscribed()) {
                subscriber.onNext(System.currentTimeMillis());
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onError(e);
                }
            }
        }
    }
}
