package com.example.home.deezertest;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.home.deezertest.activity.AlbumActivity;
import com.example.home.deezertest.activity.ArtistAlbumActivity;
import com.example.home.deezertest.activity.LoginActivity;
import com.example.home.deezertest.activity.SearchActivity;


public class NavigationUtil {

    public static final String ALBUM_ID_KEY = "ALBUM_ID_KEY";
    public static final String ALBUM_COVER_BIG_KEY = "ALBUM_COVER_BIG_KEY";
    public static final String ALBUM_TITLE_KEY = "ALBUM_TITLE_KEY";
    public static final String ARTIST_ID_KEY = "ARTIST_ID_KEY";


    //start activity med en tilbake kall (startActivityForResults)
    public static void launchSearchActivity(Activity activity) {
        Intent intent = new Intent(activity, SearchActivity.class);
        activity.startActivityForResult(intent, SearchActivity.SEARCH_REQUEST_CODE);

    }

    public static void launchLoginScreen(Activity activity) {
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        }
        activity.startActivity(intent);
    }


    public static void launchAlbumActivity(Activity activity, long albumId, String bigImageUrl, String title) {
        Bundle bundle = new Bundle();
        bundle.putLong(ALBUM_ID_KEY, albumId);
        bundle.putString(ALBUM_COVER_BIG_KEY, bigImageUrl);
        bundle.putString(ALBUM_TITLE_KEY, title);

        Intent intent = new Intent(activity, AlbumActivity.class);
        intent.putExtras(bundle);

        activity.startActivity(intent);
    }

    public static void finishActivity(Activity activity) {
        activity.finish();
    }

    public static void launchArtistAlbumActivity(Activity activity, long id) {

        Bundle bundle = new Bundle();
        bundle.putLong(ARTIST_ID_KEY, id);

        Intent intent = new Intent(activity, ArtistAlbumActivity.class);
        intent.putExtras(bundle);

        activity.startActivity(intent);
    }
}
