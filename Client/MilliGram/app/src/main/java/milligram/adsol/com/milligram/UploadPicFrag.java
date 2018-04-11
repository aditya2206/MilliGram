package milligram.adsol.com.milligram;

import android.Manifest;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.koushikdutta.async.future.Future;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.koushikdutta.ion.Response;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;

import id.zelory.compressor.Compressor;
import milligram.adsol.com.milligram.model.NetResponse;
import milligram.adsol.com.milligram.model.Photo;
import milligram.adsol.com.milligram.model.User;
import milligram.adsol.com.milligram.network.NetworkUtil;
import milligram.adsol.com.milligram.utils.Constants;
import retrofit2.adapter.rxjava.HttpException;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

import static android.app.Activity.RESULT_OK;
import static milligram.adsol.com.milligram.utils.Constants.BASE_URL;
import static milligram.adsol.com.milligram.utils.Constants.PROPICFILENAME;

/**
 * Created by adityasarma on 12/02/18.
 */

public class UploadPicFrag extends android.app.Fragment {
    public static UploadPicFrag newInstance() {
        UploadPicFrag fragment = new UploadPicFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    ImageView ivPhoto;
    Switch locationswitch;
    LinearLayout locationll;
    EditText photocaption;
    Button publishphoto;
    File compfile;
    String location = null;
    private CompositeSubscription mSubscriptions;
    View view;
    ProgressDialog dialog;
    SharedPreferences mSharedPreferences;
    public static final LatLngBounds BOUNDS_INDIA = new LatLngBounds(new LatLng(7.798000, 68.14712), new LatLng(37.090000, 97.34466));


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        view = inflater.inflate(R.layout.uploadpic_frag, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder(); StrictMode.setVmPolicy(builder.build());

        mSubscriptions = new CompositeSubscription();
        dialog = new ProgressDialog(getActivity());
        dialog.setMessage("Please Wait..  Uploading your photo..");
        dialog.setCancelable(false);



        ivPhoto = (ImageView) view.findViewById(R.id.ivPhoto);
        locationswitch = (Switch) view.findViewById(R.id.locationswitch);
        locationll = (LinearLayout) view.findViewById(R.id.locationll);
        publishphoto = (Button) view.findViewById(R.id.publishphoto);
        photocaption = (EditText) view.findViewById(R.id.photocaption);

        locationll.setVisibility(View.INVISIBLE);

        publishphoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());

                String photofilename = compfile.getName();
                String photocaption1 = photocaption.getText().toString();
                String uploadedby = mSharedPreferences.getString(Constants.NAME,"");
                String uploadedbyusername = mSharedPreferences.getString(Constants.USERNAME,"");

                Photo photo = new Photo();

                photo.setPhotofilename(photofilename);
                photo.setPhotocaption(photocaption1);
                photo.setLocation(location);
                photo.setUploadedby(uploadedby);
                photo.setPropicfilename(mSharedPreferences.getString(PROPICFILENAME,""));
                photo.setUploadedbyusername(uploadedbyusername);

                if(photocaption1.isEmpty()){
                    new CustomToast().Show_Toast(getActivity(), view, "Enter a caption for Image!");
                }else{
                    dialog.show();
                    uploadPhoto(photo);
                }



            }
        });



        locationswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                if(b){
                    locationll.setVisibility(View.VISIBLE);

                    PlaceAutocompleteFragment places= (PlaceAutocompleteFragment)
                            getChildFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);
                    places.setHint("Enter Location..");
                    places.setBoundsBias(BOUNDS_INDIA);

                    places.setOnPlaceSelectedListener(new PlaceSelectionListener() {
                        @Override
                        public void onPlaceSelected(Place place) {
                            location = (String) place.getName();
                        }

                        @Override
                        public void onError(Status status) {

                        }
                    });

                }else{
                    locationll.setVisibility(View.INVISIBLE);
                }

            }
        });

        selectImage();

        return view;
    }


    private void selectImage() {



        final CharSequence[] options = { "Take Photo", "Choose from Gallery","Cancel" };



        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setCancelable(false);

        builder.setTitle("Upload Photo!");

        builder.setItems(options, new DialogInterface.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override

            public void onClick(DialogInterface dialog, int item) {

                if (options[item].equals("Take Photo"))

                {

                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");

                    intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));

                    startActivityForResult(intent, 1);

                }

                else if (options[item].equals("Choose from Gallery"))

                {


                    Intent intent = new   Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    startActivityForResult(intent, 2);

                }

                else if (options[item].equals("Cancel")) {

                    dialog.dismiss();

                }

            }

        });

        builder.show();

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {

            if (requestCode == 1) {

                File f = new File(Environment.getExternalStorageDirectory().toString());

                for (File temp : f.listFiles()) {

                    if (temp.getName().equals("temp.jpg")) {

                        f = temp;

                        break;

                    }

                }

                try {

                    Bitmap bitmap;

                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();



                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),

                            bitmapOptions);

                    String path = android.os.Environment

                            .getExternalStorageDirectory()

                            + File.separator

                            + "MilliGram" + File.separator + "Uploads";


                    f.delete();

                    File dir = new File(path);

                    if(!dir.exists()){
                        dir.mkdirs();
                    }

                    File file = new File(path+"/"+String.valueOf(System.currentTimeMillis()) + ".jpg");
                    file.createNewFile();



                    try {

                        FileOutputStream outFile = new FileOutputStream(file);

                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outFile);

                        Bitmap compbmp = new Compressor(getActivity()).compressToBitmap(file);

                        compfile = new File(path+"/"+String.valueOf(System.currentTimeMillis()) + ".jpg");

                        FileOutputStream fos = new FileOutputStream(compfile);

                        compbmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                        Picasso.with(getActivity())
                                .load(compfile)
                                .placeholder(R.drawable.img_circle_placeholder)
                                .into(ivPhoto);

                        file.delete();

                        outFile.flush();

                        outFile.close();

                        fos.flush();

                        fos.close();

                    } catch (FileNotFoundException e) {

                        e.printStackTrace();

                    } catch (IOException e) {

                        e.printStackTrace();

                    } catch (Exception e) {

                        e.printStackTrace();

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                }

            } else if (requestCode == 2) {

                try {

                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), data.getData());

                    Uri tempUri = getImageUri(getActivity().getApplicationContext(), bitmap);
                    String path = getRealPathFromURI(tempUri);

                    File file = new File(path);

                    compfile = new Compressor(getActivity()).compressToFile(file);

                    Picasso.with(getActivity())
                            .load(compfile)
                            .placeholder(R.drawable.img_circle_placeholder)
                            .into(ivPhoto);

                    Log.d("File Path:",compfile.getPath());
                    Log.d("File Size: " ,getReadableFileSize(compfile.length()));

                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

        }

    }


    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }

    public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }

    public String getReadableFileSize(long size) {
        if (size <= 0) {
            return "0";
        }
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }


    private void uploadPhoto(Photo photo) {


        mSubscriptions.add(NetworkUtil.getRetrofit().uploadphoto(photo)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(this::handleResponse,this::handleError));
    }

    private void handleResponse(NetResponse response) {

        showSnackBarMessage(response.getMessage());
        uploadpicfile();
    }

    private void handleError(Throwable error) {

        dialog.dismiss();

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

    private void uploadpicfile(){

        Future uploading = Ion.with(getActivity())
                .load(BASE_URL+"uploadphotosfile")
                .setMultipartFile("image", compfile)
                .asString()
                .withResponse()
                .setCallback(new FutureCallback<Response<String>>() {
                    @Override
                    public void onCompleted(Exception e, Response<String> result) {
                        try {
                            JSONObject jobj = new JSONObject(result.getResult());
                            Toast.makeText(getActivity(), jobj.getString("message"), Toast.LENGTH_SHORT).show();


                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }

                    }
                });


        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        dialog.dismiss();
        goToHome();


    }

    private void showSnackBarMessage(String message) {

        if (view != null) {

            Snackbar.make(view,message,Snackbar.LENGTH_SHORT).show();
        }
    }


    private void goToHome(){

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_layout, HomeFrag.newInstance());
        transaction.commit();
    }


}
