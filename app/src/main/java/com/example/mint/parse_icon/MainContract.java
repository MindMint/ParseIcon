package com.example.mint.parse_icon;

/**
 * Created by mint on 2018/4/16.
 */

public class MainContract {
    public interface IMainView {
        void showProgress();
        void hideProgress();
        void onParseSuccess(String url);
        void onParseError(Throwable t);
    }

    public interface IParsePresenter {
        void parseIcon(String url);
        void unsubscribe();
    }
}
