package milligram.adsol.com.milligram;

import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import de.hdodenhof.circleimageview.CircleImageView;
import milligram.adsol.com.milligram.model.NetResponse;
import milligram.adsol.com.milligram.model.User;
import milligram.adsol.com.milligram.network.NetworkUtil;
import milligram.adsol.com.milligram.utils.Constants;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static milligram.adsol.com.milligram.utils.Constants.BASE_URL;
import static milligram.adsol.com.milligram.utils.Constants.PROPICFILENAME;

public class ViewUser extends AppCompatActivity {

    TextView apptitle;
    TabLayout tlUserProfileTabs;
    RecyclerView rvUserProfile;
    Bundle bundle;
    String name;
    CompositeSubscription mSubscriptions;
    View view;
    int something = 0;
    CircleImageView propic;
    TextView name1,username1,biotv1,noofposts1;
    ImageView verified1;
    ArrayList<String> photofilename,photocaption,location,uploadedby,propicfilename,created_at;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        apptitle = (TextView) findViewById(R.id.apptitle);
        Typeface mycustomfont = Typeface.createFromAsset(getAssets(),"fonts/billabong.ttf");
        apptitle.setTypeface(mycustomfont);
        apptitle.setText("milliGram");

        bundle = getIntent().getExtras();
        name = bundle.getString("username");

        tlUserProfileTabs = (TabLayout) findViewById(R.id.tlUserProfileTabs1);
        rvUserProfile = (RecyclerView) findViewById(R.id.rvUserProfile1);
        propic = (CircleImageView) findViewById(R.id.ivUserProfilePhoto1);
        name1 = (TextView) findViewById(R.id.name1);
        username1 = (TextView) findViewById(R.id.username1);
        biotv1 = (TextView) findViewById(R.id.biotv1);
        noofposts1 = (TextView) findViewById(R.id.noofposts1);
        verified1 = (ImageView) findViewById(R.id.verified1);
        mSubscriptions = new CompositeSubscription();
        view = findViewById(android.R.id.content);
        photofilename = new ArrayList<>();
        photocaption = new ArrayList<>();
        location = new ArrayList<>();
        uploadedby = new ArrayList<>();
        propicfilename = new ArrayList<>();
        created_at = new ArrayList<>();
        verified1.setVisibility(View.INVISIBLE);

        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));

        getSingleUserProfile(name);
        setupUserProfileGrid();

    }

    private void setupUserProfileGrid() {

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                //userPhotosAdapter.setLockedAnimations(true);
            }
        });
    }



    private void getSingleUserProfile(String name) {

        mSubscriptions.add(NetworkUtil.getRetrofit().getSingleuserprofile(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(NetResponse netResponse) {

        try {

            JSONObject jsonrootobj = new JSONObject(netResponse.getMessage());
            JSONArray jsonarray = jsonrootobj.getJSONArray("user");
            String nameee = null,username = null,bio = null,propicfilename = null,verified = null;

            for(int i = 0;i<jsonarray.length();i++){
                JSONObject jsonobj = jsonarray.getJSONObject(i);
                nameee = jsonobj.optString("name");
                username = jsonobj.optString("username");
                bio = jsonobj.optString("bio");
                propicfilename = jsonobj.optString("propicfilename");
                verified = jsonobj.optString("verified");
                something = something + 1;


            }


            name1.setText(nameee);
            username1.setText("@"+username);
            biotv1.setText(bio);

            if(verified.equals("true")){
                verified1.setVisibility(View.VISIBLE);
            }

            Picasso.with(ViewUser.this)
                    .load(BASE_URL+"getpropic/"+propicfilename)
                    .placeholder(R.drawable.img_circle_placeholder)
                    .into(propic);



            getSingleUserPhotos(username);

//            mRecyclerView.setHasFixedSize(true);
//            mLayoutManager = new LinearLayoutManager(getActivity());
//            mRecyclerView.setLayoutManager(mLayoutManager);
//            mAdapter = new MyRecyclerViewAdapter(getDataSet());
//            mRecyclerView.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                NetResponse response = gson.fromJson(errorBody,NetResponse.class);
                showSnackBarMessage(response.getMessage());



            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            showSnackBarMessage("Network Error !");
        }
    }


    private void getSingleUserPhotos(String name) {

        mSubscriptions.add(NetworkUtil.getRetrofit().getSingleUserPhotos(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse2,this::handleError2));
    }

    private void handleResponse2(NetResponse response) {

        try {

            JSONObject jsonrootobj = new JSONObject(response.getMessage());
            JSONArray jsonarray = jsonrootobj.getJSONArray("photos");


            for(int i = 0;i<jsonarray.length();i++){
                JSONObject jsonobj = jsonarray.getJSONObject(i);
                photofilename.add(jsonobj.optString("photofilename"));
                photocaption.add(jsonobj.optString("photocaption"));
                location.add(jsonobj.optString("location"));
                uploadedby.add(jsonobj.optString("uploadedby"));
                propicfilename.add(jsonobj.optString("propicfilename"));
                created_at.add(jsonobj.optString("created_at"));
                something = something + 1;


            }

            Collections.reverse(photofilename);
            Collections.reverse(photocaption);
            Collections.reverse(location);
            Collections.reverse(uploadedby);
            Collections.reverse(propicfilename);
            Collections.reverse(created_at);

            UserProfileAdapter userPhotosAdapter = new UserProfileAdapter(ViewUser.this,photofilename,photocaption,location,uploadedby,propicfilename,created_at);
            setupUserProfileGrid();
            rvUserProfile.setAdapter(userPhotosAdapter);

            noofposts1.setText(String.valueOf(photofilename.size()));



        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleError2(Throwable error) {



        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                NetResponse response = gson.fromJson(errorBody,NetResponse.class);
                //new CustomToast().Show_Toast(getActivity(), view, response.getMessage());
                Toast.makeText(ViewUser.this, response.getMessage(), Toast.LENGTH_SHORT).show();

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            //new CustomToast().Show_Toast(getActivity(), view, "Network Error!");
        }
    }


    private void showSnackBarMessage(String message) {

        if (view != null) {

            Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
        }
    }


}
