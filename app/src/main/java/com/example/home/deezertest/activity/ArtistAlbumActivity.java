package com.example.home.deezertest.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.model.Album;
import com.deezer.sdk.model.PlayableEntity;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.deezer.sdk.player.event.PlayerWrapperListener;
import com.example.home.deezertest.NavigationUtil;
import com.example.home.deezertest.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class ArtistAlbumActivity extends PlayerActivity implements PlayerWrapperListener{

    /** The list of albums of displayed by this activity. */
    private ArrayList<Album> mAlbumsList = new ArrayList<Album>();

    /** the Albums list adapter */
    private ArrayAdapter<Album> mAlbumsAdapter;



    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // restore existing deezer Connection
        new SessionStore().restore(mDeezerConnect, this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Albums");

        // Setup the UI
        setContentView(R.layout.activity_album_list);


        //initRecyclerView();

        setupAlbumsList();

        // fetch albums list
        getArtistAlbums();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    /**
     * Setup the List UI
     */
    private void setupAlbumsList() {

         mAlbumsAdapter = new ArrayAdapter<Album>(this, R.layout.item_title_cover, mAlbumsList) {

            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                Album album = getItem(position);
                View view = convertView;
                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = vi.inflate(R.layout.item_title_cover, null);
                }

                TextView albumTitle = (TextView) view.findViewById(android.R.id.text1);
                albumTitle.setText(album.getTitle());

//                TextView albumArtist = (TextView) view.findViewById(R.id.textViewArtistName);
//                albumArtist.setText(album.getArtist().getName()); //nullpointer

                ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
                Picasso.with(ArtistAlbumActivity.this).load(album.getMediumImageUrl()).into(imageView);

                return view;
            }
        };



        GridView gridview = (GridView) findViewById(android.R.id.list);
        gridview.setAdapter(mAlbumsAdapter);
        gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                Album album = mAlbumsList.get(position);
                NavigationUtil.launchAlbumActivity(ArtistAlbumActivity.this, album.getId(), album.getBigImageUrl(), album.getTitle());
            }
        });
    }


    /**
     * Search for all albums splitted by genre
     */
    private void getArtistAlbums() {

        Bundle bundle = getIntent().getExtras();
        long albumId = -1;
        if (bundle != null) {
            albumId = bundle.getLong(NavigationUtil.ARTIST_ID_KEY);
        }
//        long artistAlbumsId = 13; //eminem
        final DeezerRequest request = DeezerRequestFactory.requestArtistAlbums(albumId);
        mDeezerConnect.requestAsync(request, new JsonRequestListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(final Object result, final Object requestId) {

                mAlbumsList.clear();

                try {
                    mAlbumsList.addAll((List<Album>) result);
                }
                catch (ClassCastException e) {
                    handleError(e);
                }

                if (mAlbumsList.isEmpty()) {
                    Toast.makeText(ArtistAlbumActivity.this, getResources()
                            .getString(R.string.no_results), Toast.LENGTH_LONG).show();
                }

                mAlbumsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onUnparsedResult(final String response, final Object requestId) {
                handleError(new DeezerError("Unparsed reponse"));
            }


            @Override
            public void onException(final Exception exception,
                                    final Object requestId) {
                handleError(exception);
            }


        });



    }

    @Override
    public void onAllTracksEnded() {

    }

    @Override
    public void onPlayTrack(PlayableEntity playableEntity) {

    }


    @Override
    public void onTrackEnded(PlayableEntity playableEntity) {

    }

    @Override
    public void onRequestException(Exception e, Object o) {
        handleError(e);
    }
}
