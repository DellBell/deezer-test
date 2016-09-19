package com.example.home.deezertest.activity;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.deezer.sdk.model.Artist;
import com.deezer.sdk.network.connect.SessionStore;
import com.deezer.sdk.network.request.DeezerRequest;
import com.deezer.sdk.network.request.DeezerRequestFactory;
import com.deezer.sdk.network.request.event.DeezerError;
import com.deezer.sdk.network.request.event.JsonRequestListener;
import com.example.home.deezertest.NavigationUtil;
import com.example.home.deezertest.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends MainActivity {

    public static final String SEARCH_BUNDLE_KEY = "SEARCH_BUNDLE_KEY";
    public static final int SEARCH_REQUEST_CODE = 123;
    public static final int SEARCH_RESULT_CODE = 234;

    /** The list of albums of displayed by this activity. */
    private ArrayList<Artist> mArtistsList = new ArrayList<Artist>();

    /** the Albums list adapter */
    private ArrayAdapter<Artist> mArtistAdapter;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_list);


        // restore existing deezer Connection
        new SessionStore().restore(mDeezerConnect, this);
        setupAlbumsList();




        sendButton();

    }

    /**
     * Setup the List UI
     */
    private void setupAlbumsList() {

        mArtistAdapter = new ArrayAdapter<Artist>(this, R.layout.item_artist_search, mArtistsList) {

            @Override
            public View getView(final int position, final View convertView, final ViewGroup parent) {
                Artist artist = getItem(position);
                View view = convertView;
                if (view == null) {
                    LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    view = vi.inflate(R.layout.item_artist_search, null);
                }

                TextView artistName = (TextView) view.findViewById(android.R.id.text1);
                artistName.setText(artist.getName());

                ImageView imageView = (ImageView) view.findViewById(android.R.id.icon);
                Picasso.with(SearchActivity.this).load(artist.getPictureUrl()).into(imageView);

                return view;
            }
        };

        ListView listView = (ListView) findViewById(android.R.id.list);
        listView.setAdapter(mArtistAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(final AdapterView<?> parent, final View view,
                                    final int position, final long id) {
                Artist artist = mArtistsList.get(position);
                NavigationUtil.launchArtistAlbumActivity(SearchActivity.this, artist.getId());
            }
        });
    }

    /**
     * Search for all albums splitted by genre
     */
    private void getArtistAlbums() {

        String artistName = searchArtist();
        final DeezerRequest request = DeezerRequestFactory.requestSearchArtists(artistName);
        mDeezerConnect.requestAsync(request, new JsonRequestListener() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResult(final Object result, final Object requestId) {

                mArtistsList.clear();

                try {
                    mArtistsList.addAll((List<Artist>) result);
                }
                catch (ClassCastException e) {
                    handleError(e);
                }

                if (mArtistsList.isEmpty()) {
                    Toast.makeText(SearchActivity.this, getResources()
                            .getString(R.string.no_results), Toast.LENGTH_LONG).show();
                }

                mArtistAdapter.notifyDataSetChanged();
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

    private String searchArtist() {
        EditText editText = (EditText) findViewById(R.id.editTextSearchArtist);
        String message = editText.getText().toString();
        return message;
    }

    private void sendButton() {
        Button start = (Button) findViewById(R.id.buttonSearch);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                getArtistAlbums();
            }
        });

    }
}
