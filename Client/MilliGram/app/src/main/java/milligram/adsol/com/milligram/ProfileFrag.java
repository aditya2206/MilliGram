package milligram.adsol.com.milligram;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import de.hdodenhof.circleimageview.CircleImageView;
import milligram.adsol.com.milligram.model.EditBio;
import milligram.adsol.com.milligram.model.NetResponse;
import milligram.adsol.com.milligram.model.PhotosArray;
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

public class ProfileFrag extends android.app.Fragment {
    public static ProfileFrag newInstance() {
        ProfileFrag fragment = new ProfileFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    TabLayout tlUserProfileTabs;
    PhotosArray photosArray;
    Button logout,editprofile;
    private static TextView noofposts;
    private static TextView name,username,biotv;
    private static CircleImageView ivUserProfilePhoto;
    private static ImageView verified;
    private static String mToken,mEmail;
    private CompositeSubscription mSubscriptions;
    SharedPreferences mSharedPreferences;
    private UserProfileAdapter userPhotosAdapter;
    View view;
    ArrayList<String> photofilename,arrayList,photocaption,location,uploadedby,propicfilename,created_at;
    int something = 0;
    RecyclerView rvUserProfile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.profile_frag, container, false);
        tlUserProfileTabs = (TabLayout) view.findViewById(R.id.tlUserProfileTabs);
        logout = (Button) view.findViewById(R.id.logout);
        editprofile = (Button) view.findViewById(R.id.editprofile);
        rvUserProfile = (RecyclerView) view.findViewById(R.id.rvUserProfile);
        name = (TextView) view.findViewById(R.id.name);
        username = (TextView) view.findViewById(R.id.username);
        biotv = (TextView) view.findViewById(R.id.biotv);
        ivUserProfilePhoto = (CircleImageView) view.findViewById(R.id.ivUserProfilePhoto);
        verified = (ImageView) view.findViewById(R.id.verified);
        noofposts = (TextView) view.findViewById(R.id.noofposts);
        photosArray = new PhotosArray();
        mSubscriptions = new CompositeSubscription();

        arrayList = new ArrayList<>();
        photofilename = new ArrayList<>();
        photocaption = new ArrayList<>();
        location = new ArrayList<>();
        uploadedby = new ArrayList<>();
        propicfilename = new ArrayList<>();
        created_at = new ArrayList<>();


        verified.setVisibility(View.INVISIBLE);
        name.setText("");
        username.setText("");
        biotv.setText("");


        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_grid_on_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_list_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_place_white));
        tlUserProfileTabs.addTab(tlUserProfileTabs.newTab().setIcon(R.drawable.ic_label_white));

        editprofile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Edit Bio");
                alertDialog.setMessage("Are you sure you want to change your Bio?");
                alertDialog.setCancelable(false);

                final EditText input = new EditText(getActivity());
                input.setHint("Write something about yourself..");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);
                alertDialog.setIcon(R.mipmap.ic_launcher_round);

                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                String editbio1 = input.getText().toString();
                                User user = new User();
                                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                String email = mSharedPreferences.getString(Constants.EMAIL,"");

                                EditBio editBio = new EditBio();
                                editBio.setBio(editbio1);
                                editBio.setEmail(email);
                                EditbioMethod(editBio);

                            }
                        });

                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();


            }
        });


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
                alertDialogBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        SharedPreferences.Editor editor = mSharedPreferences.edit();
                        editor.putString(Constants.EMAIL,"");
                        editor.putString(Constants.TOKEN,"");
                        editor.putString(Constants.NAME,"");
                        editor.putString(Constants.USERNAME,"");
                        editor.putString(PROPICFILENAME,"");
                        editor.apply();
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    }
                });

                alertDialogBuilder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //Dont Logout...
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.setCancelable(false);
                alertDialog.setIcon(R.drawable.logout);
                alertDialog.setTitle("Logout?");
                alertDialog.setMessage("Are you sure want to Logout?");
                alertDialog.show();



            }
        });

        initSharedPreferences();
        loadProfile();
        //setupUserProfileGrid();

        return view;
    }

    private void setupUserProfileGrid() {

        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        rvUserProfile.setLayoutManager(layoutManager);
        rvUserProfile.setOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                userPhotosAdapter.setLockedAnimations(true);
            }
        });
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

        name.setText(user.getName());
        username.setText("@"+user.getUsername());
        biotv.setText(user.getBio());

        String confirmverification = user.getVerified();

        if(confirmverification.equals("true")){
            verified.setVisibility(View.VISIBLE);
        }

        Picasso.with(getActivity())
                .load(BASE_URL+"getpropic/"+user.getPropicfilename())
                .placeholder(R.drawable.img_circle_placeholder)
                .into(ivUserProfilePhoto);
        getSingleUserPhotos();

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

    private void EditbioMethod(EditBio editBio) {

        mSubscriptions.add(NetworkUtil.getRetrofit().editbio(editBio)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse1,this::handleError1));
    }

    private void handleResponse1(NetResponse response) {

        showSnackBarMessage(response.getMessage());
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, new ProfileFrag());
        transaction.commit();

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


    private void getSingleUserPhotos() {

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String mName = mSharedPreferences.getString(Constants.USERNAME,"");

        mSubscriptions.add(NetworkUtil.getRetrofit().getSingleUserPhotos(mName)
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

            userPhotosAdapter = new UserProfileAdapter(getActivity(),photofilename,photocaption,location,uploadedby,propicfilename,created_at);
            setupUserProfileGrid();
            rvUserProfile.setAdapter(userPhotosAdapter);

            noofposts.setText(String.valueOf(photofilename.size()));



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
                Toast.makeText(getActivity(), response.getMessage(), Toast.LENGTH_SHORT).show();

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
