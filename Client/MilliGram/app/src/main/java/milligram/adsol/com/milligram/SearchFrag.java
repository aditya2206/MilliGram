package milligram.adsol.com.milligram;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import milligram.adsol.com.milligram.model.NetResponse;
import milligram.adsol.com.milligram.network.NetworkUtil;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static milligram.adsol.com.milligram.utils.Constants.BASE_URL;

/**
 * Created by adityasarma on 06/02/18.
 */

public class SearchFrag extends android.app.Fragment {
    public static SearchFrag newInstance() {
        SearchFrag fragment = new SearchFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    AutoCompleteTextView searchbar;
    ArrayAdapter arrayAdapter;
    ArrayList<String> usersname,usersusername;
    View view;
    CompositeSubscription mSubscriptions;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view =  inflater.inflate(R.layout.search_frag, container, false);
        searchbar = (AutoCompleteTextView) view.findViewById(R.id.searchbar);
        usersname = new ArrayList<>();
        usersusername = new ArrayList<>();
        mSubscriptions = new CompositeSubscription();
        arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,usersname);

        searchbar.setAdapter(arrayAdapter);

        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                if(charSequence.toString().equals("")){

                }else{
                    getSingleUserProfile(String.valueOf(charSequence));
                }

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchbar.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(),ViewUser.class);
                intent.putExtra("username",usersusername.get(i));
                view.getContext().startActivity(intent);
            }
        });

        return view;


    }


    private void getSingleUserProfile(String name) {

        mSubscriptions.add(NetworkUtil.getRetrofit().searchUsersList(name)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(NetResponse netResponse) {

        try {

            JSONObject jsonrootobj = new JSONObject(netResponse.getMessage());
            JSONArray jsonarray = jsonrootobj.getJSONArray("user");

            usersname.clear();
            usersusername.clear();

            for(int i = 0;i<jsonarray.length();i++){
                JSONObject jsonobj = jsonarray.getJSONObject(i);
                usersname.add(jsonobj.optString("name")+"\t"+"( "+jsonobj.optString("username")+" )");
                usersusername.add(jsonobj.optString("username"));

            }

            arrayAdapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1,usersname);

            searchbar.setAdapter(arrayAdapter);


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


}