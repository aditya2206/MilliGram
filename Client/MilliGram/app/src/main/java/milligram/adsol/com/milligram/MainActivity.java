package milligram.adsol.com.milligram;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import milligram.adsol.com.milligram.utils.Constants;


public class MainActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    private SharedPreferences mSharedPreferences;


    @Override
    public void onBackPressed(){
        SignUp_Fragment myFragment = (SignUp_Fragment) getSupportFragmentManager().findFragmentByTag("SignUp_Fragment");
        if (myFragment != null && myFragment.isVisible()) {
            // add your code here
            getSupportFragmentManager().beginTransaction()
                    .setCustomAnimations(R.anim.right_enter, R.anim.left_out)
                    .replace(R.id.frameContainer, new Login_Fragment(), Utils.Login_Fragment)
                    .commit();
        }else {
            super.onBackPressed();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {


        }else{
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1234);

        }
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        String mToken = mSharedPreferences.getString(Constants.TOKEN,"");
        String mEmail = mSharedPreferences.getString(Constants.EMAIL,"");

        if(mToken.isEmpty() || mEmail.isEmpty()){
            fragmentManager = getSupportFragmentManager();

            fragmentManager
                    .beginTransaction()
                    .add(R.id.frameContainer, new Login_Fragment(),
                            Utils.Login_Fragment).commit();
        }else{
            Intent intent = new Intent(MainActivity.this,HomePageActivity.class);
            startActivity(intent);
            finish();
        }




    }
}
