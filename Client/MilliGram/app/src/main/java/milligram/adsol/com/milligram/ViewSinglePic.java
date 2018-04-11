package milligram.adsol.com.milligram;

import android.app.ActionBar;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import de.hdodenhof.circleimageview.CircleImageView;
import milligram.adsol.com.milligram.utils.Constants;

public class ViewSinglePic extends AppCompatActivity {

    TextView apptitle;
    Bundle bundle;
    String propicfilename,uploadedby,location,photofilename,photocaption,created_at;
    CircleImageView viewsinglepicpropic;
    TextView viewsinglepicname,viewsinglepiclocation,viewsinglepiccaption,viewsinglepictime;
    ImageView viewsinglepicimage;
    HashTagHelper mTextHashTagHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_single_pic);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSupportActionBar().setCustomView(R.layout.abs_layout);

        apptitle = (TextView) findViewById(R.id.apptitle);
        Typeface mycustomfont = Typeface.createFromAsset(getAssets(),"fonts/billabong.ttf");
        apptitle.setTypeface(mycustomfont);
        apptitle.setText("milliGram");

        viewsinglepicpropic = (CircleImageView) findViewById(R.id.homepropic2);
        viewsinglepicimage = (ImageView) findViewById(R.id.homeimage2);
        viewsinglepicname = (TextView) findViewById(R.id.homename2);
        viewsinglepiclocation = (TextView) findViewById(R.id.homelocation2);
        viewsinglepiccaption = (TextView) findViewById(R.id.homecaption2);
        viewsinglepictime = (TextView) findViewById(R.id.hometime2);
        mTextHashTagHelper = HashTagHelper.Creator.create(getResources().getColor(R.color.text_like_counter), null);



        bundle = getIntent().getExtras();
        propicfilename = bundle.getString("propicfilename");
        uploadedby = bundle.getString("uploadedby");
        location = bundle.getString("location");
        photofilename = bundle.getString("photofilename");
        photocaption = bundle.getString("photocaption");
        created_at = bundle.getString("created_at");

        Glide.with(getApplicationContext()).load(Constants.BASE_URL+"getpropic/"+propicfilename).into(viewsinglepicpropic);
        Glide.with(getApplicationContext()).load(Constants.BASE_URL+"getparticularpic/"+photofilename).into(viewsinglepicimage);
        viewsinglepicname.setText(uploadedby);
        viewsinglepiclocation.setText(location);
        viewsinglepiccaption.setText(photocaption);
        viewsinglepictime.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(created_at)));
        mTextHashTagHelper.handle(viewsinglepiccaption);

    }
}
