package com.example.mint.parse_icon;

import com.example.mint.model.HttpHelper;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

/**
 * Created by mint on 2018/4/12.
 */

public class ParsePresenter implements MainContract.IParsePresenter {
    private MainContract.IMainView mainView;
    public ParsePresenter(MainContract.IMainView mainView) {
        this.mainView = mainView;
    }
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void parseIcon(String url) {
        mainView.showProgress();
        Disposable disposable = HttpHelper.getInstance().parseIcon(url).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                mainView.onParseSuccess(s);
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mainView.onParseError(throwable);
                mainView.hideProgress();
            }
        }, new Action() {
            @Override
            public void run() throws Exception {
                mainView.hideProgress();
            }
        });
        compositeDisposable.add(disposable);
    }

    @Override
    public void unsubscribe() {
        compositeDisposable.dispose();
    }

    //    @Override
//    public void subscribe() {
//        HttpHelper.getInstance().parseIcon(url).subscribe(new Consumer<String>() {
//            @Override
//            public void accept(String s) throws Exception {
//                Toast.makeText(MainActivity.this, s, Toast.LENGTH_SHORT).show();
//            }
//        }, new Consumer<Throwable>() {
//            @Override
//            public void accept(Throwable throwable) throws Exception {
//                Toast.makeText(MainActivity.this, throwable.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        }, new Action() {
//            @Override
//            public void run() throws Exception {
//                Toast.makeText(MainActivity.this, "complete", Toast.LENGTH_SHORT).show();
//            }
//        });
//    }
//
//    @Override
//    public void unsubscribe() {
//
//    }
}
