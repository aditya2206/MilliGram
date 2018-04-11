package milligram.adsol.com.milligram;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import milligram.adsol.com.milligram.model.EditBio;
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

/**
 * Created by adityasarma on 06/02/18.
 */

public class HomeFrag extends android.app.Fragment {
    public static HomeFrag newInstance() {
        HomeFrag fragment = new HomeFrag();
        return fragment;
    }

    private CompositeSubscription mSubscriptions;
    View view;
    ArrayList<String> photofilename,photocaption,location,uploadedby,propicfilename,created_at,uploadedbyUsername;
    public RecyclerView mRecyclerView;
    public RecyclerView.Adapter mAdapter;
    public RecyclerView.LayoutManager mLayoutManager;
    int something = 0;
    String mToken,mEmail;
    SharedPreferences mSharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.home_frag, container, false);

        photofilename = new ArrayList<String>();
        photocaption = new ArrayList<String>();
        location = new ArrayList<String>();
        uploadedby = new ArrayList<String>();
        propicfilename = new ArrayList<String>();
        created_at = new ArrayList<String>();
        uploadedbyUsername = new ArrayList<>();

        mSubscriptions = new CompositeSubscription();
        mRecyclerView = (RecyclerView) view.findViewById(R.id.rvFeed);
        mRecyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mAdapter = new MyRecyclerViewAdapter(getDataSet());
        mRecyclerView.setAdapter(mAdapter);

        initSharedPreferences();
        loadProfile();
        getPhotos();

        return view;
    }


    private void initSharedPreferences() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        mEmail = mSharedPreferences.getString(Constants.EMAIL,"");
    }

    private void loadProfile() {

        mSubscriptions.add(NetworkUtil.getRetrofit(mToken).getProfile(mEmail)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(User user) {

        SharedPreferences.Editor editor = mSharedPreferences.edit();
        editor.putString(Constants.USERNAME,user.getUsername());
        editor.putString(Constants.NAME,user.getName());
        editor.putString(PROPICFILENAME,user.getPropicfilename());
        editor.apply();


    }

    private void handleError(Throwable error) {


        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                NetResponse response = gson.fromJson(errorBody,NetResponse.class);
                if(response.getMessage().equals("Invalid Token !")){
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putString(Constants.EMAIL,"");
                    editor.putString(Constants.TOKEN,"");
                    editor.putString(Constants.NAME,"");
                    editor.putString(Constants.USERNAME,"");
                    editor.putString(PROPICFILENAME,"");
                    editor.apply();
                    Toast.makeText(getActivity(), "Your session has expired please login again!", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(getActivity(),MainActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            new CustomToast().Show_Toast(getActivity(), view, "Network Error!");
        }
    }


    private void getPhotos() {

        mSubscriptions.add(NetworkUtil.getRetrofit().getPhotos()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1,this::handleError1));
    }

    private void handleResponse1(NetResponse response) {

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
                uploadedbyUsername.add(jsonobj.optString("uploadedbyUsername"));
                something = something + 1;


            }

            Collections.reverse(photofilename);
            Collections.reverse(photocaption);
            Collections.reverse(location);
            Collections.reverse(uploadedby);
            Collections.reverse(propicfilename);
            Collections.reverse(created_at);
            Collections.reverse(uploadedbyUsername);


            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);
            mAdapter = new MyRecyclerViewAdapter(getDataSet());
            mRecyclerView.setAdapter(mAdapter);


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void handleError1(Throwable error) {



        if (error instanceof HttpException) {

            Gson gson = new GsonBuilder().create();

            try {

                String errorBody = ((HttpException) error).response().errorBody().string();
                NetResponse response = gson.fromJson(errorBody,NetResponse.class);
                new CustomToast().Show_Toast(getActivity(), view, response.getMessage());

            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {

            new CustomToast().Show_Toast(getActivity(), view, "Network Error!");
        }
    }




    private void showSnackBarMessage(String message) {

        if (view != null) {

            Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
        }
    }


    private ArrayList<DataParser> getDataSet() {

        ArrayList results = new ArrayList<DataParser>();

        for (int index = 0;index<something ; index++) {
            DataParser obj = new DataParser(photofilename.get(index), photocaption.get(index), location.get(index), uploadedby.get(index), propicfilename.get(index), created_at.get(index), uploadedbyUsername.get(index));
            results.add(index, obj);

        }
        return results;
    }

}
