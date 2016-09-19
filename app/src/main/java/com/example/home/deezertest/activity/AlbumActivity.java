package com.example.home.deezertest.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.model.Track;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.example.home.deezertest.NavigationUtil;
import com.example.home.deezertest.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;


public class AlbumActivity extends MainActivity {

    private ArrayList<Track> mTrackList = new ArrayList<Track>();
    private ArrayAdapter<Track> mTracksAdapter;

    private String toolbarTitle = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_track_list);

        // restore existing deezer Connection
        new SessionStore().restore(mDeezerConnect, this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            toolbarTitle = bundle.getString(NavigationUtil.ALBUM_TITLE_KEY);
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbarTrack);
        setSupportActionBar(toolbar);

        TextView toolbarAlbumTitle = (TextView) findViewById(R.id.toolbar_title);
        toolbarAlbumTitle.setText(toolbarTitle);

        // Setup the UI
        setupTracksList();


        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mTracksAdapter);

        // Get album ID
        getTracksAlbumId();
    }

    /**
     * Setup the List UI
     */
    private void setupTracksList() {

        mTracksAdapter = new ArrayAdapter<Track>(this, R.layout.item_track_cover, mTrackList) {

            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                Track track = getItem(position);

                View view = convertView;
                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = vi.inflate(R.layout.item_track_cover, parent, false);
                }

                TextView trackPosition = (TextView) view.findViewById(R.id.trackPosition);
                trackPosition.setText(String.format("%s.", String.valueOf(track.getTrackPosition())));

                TextView trackTitle = (TextView) view.findViewById(android.R.id.text2);
                trackTitle.setText(track.getTitle());

                TextView trackDuration = (TextView) view.findViewById(R.id.textViewTrackDuration);

                trackDuration.setText(String.valueOf(secondsToString(track.getDuration())));

                Bundle bundle = getIntent().getExtras();
                String albumBigCover = "NA";
                if (bundle != null) {
                    albumBigCover = bundle.getString(NavigationUtil.ALBUM_COVER_BIG_KEY);
                }

//                ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
//                Picasso.with(getContext()).load(albumBigCover).into(imageView); //nullpointer

                return view;
            }
        };
    }

    private String secondsToString(int seconds) {
        return String.format("%02d:%02d", seconds / 60, seconds % 60);
    }

    public void getTracksAlbumId() {
        Bundle bundle = getIntent().getExtras();
        long albumId = -1;
        if (bundle != null) {
            albumId = bundle.getLong(NavigationUtil.ALBUM_ID_KEY);
        }
        final DeezerRequest request = DeezerRequestFactory.requestAlbumTracks(albumId);
        mDeezerConnect.requestAsync(request, new JsonRequestListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(final Object result, final Object requestId) {

                mTrackList.clear();

                try {
                    mTrackList.addAll((List<Track>) result);
                }
                catch (ClassCastException e) {
                    handleError(e);
                }

                if (mTrackList.isEmpty()) {
                    Toast.makeText(AlbumActivity.this, getResources()
                            .getString(R.string.no_results), Toast.LENGTH_LONG).show();
                }

                mTracksAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUnparsedResult(final String response, final Object requestId) {
                handleError(new DeezerError("Unparsed reponse"));
            }

            @Override
            public void onException(final Exception exception, final Object requestId) {
                handleError(exception);
            }
        });
    }
}
