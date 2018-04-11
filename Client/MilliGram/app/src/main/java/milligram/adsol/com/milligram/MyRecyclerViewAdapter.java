package milligram.adsol.com.milligram;

import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.volokh.danylo.hashtaghelper.HashTagHelper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;

import de.hdodenhof.circleimageview.CircleImageView;

import static milligram.adsol.com.milligram.utils.Constants.BASE_URL;

/**
 * Created by adityasarma on 26/02/18.
 */

public class MyRecyclerViewAdapter extends RecyclerView
        .Adapter<MyRecyclerViewAdapter
        .DataObjectHolder> {


    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private ArrayList<DataParser> mDataset;
    private static MyClickListener myClickListener;



    public static class DataObjectHolder extends RecyclerView.ViewHolder
            implements View
            .OnClickListener {


        TextView homename,homelocation,homecaption,hometime;
        ImageView homeimage;
        ImageButton btnLike,btnMore;
        CircleImageView homepropic;
        TextView nooflikes;
        TextSwitcher tsLikesCounter;
        HashTagHelper mTextHashTagHelper;


        public DataObjectHolder(View itemView) {
            super(itemView);

            nooflikes = (TextView) itemView.findViewById(R.id.nooflikes);
            tsLikesCounter = (TextSwitcher) itemView.findViewById(R.id.tsLikesCounter);
            btnLike = (ImageButton) itemView.findViewById(R.id.btnLike);
            btnMore = (ImageButton) itemView.findViewById(R.id.btnMore);
            homename = (TextView) itemView.findViewById(R.id.homename);
            homelocation = (TextView) itemView.findViewById(R.id.homelocation);
            homecaption = (TextView) itemView.findViewById(R.id.homecaption);
            hometime = (TextView) itemView.findViewById(R.id.hometime);
            homeimage = (ImageView) itemView.findViewById(R.id.homeimage);
            homepropic = (CircleImageView) itemView.findViewById(R.id.homepropic);

        }


        @Override
        public void onClick(View v) {
            myClickListener.onItemClick(getAdapterPosition(), v);
        }
    }

    public void setOnItemClickListener(MyClickListener myClickListener) {
        this.myClickListener = myClickListener;
    }

    public MyRecyclerViewAdapter(ArrayList<DataParser> myDataset) {
        mDataset = myDataset;
    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_feed, parent, false);


        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }


    @Override
    public void onBindViewHolder(DataObjectHolder holder, int position) {


        final boolean[] showingFirst = {true};
        final long[] lastTouchTime = {0};
        final long[] currentTouchTime = {0};

        holder.btnLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String likes = String.valueOf(holder.nooflikes.getText());
                String numberOnly= likes.replaceAll("[^0-9]", "");
                int nooflikesint = Integer.parseInt(numberOnly);

                if(showingFirst[0] == true){
                    holder.btnLike.setImageResource(R.drawable.redheart);
                    nooflikesint++;
                    holder.nooflikes.setText(String.valueOf(nooflikesint)+" likes");
                    showingFirst[0] = false;
                }else{
                    holder.btnLike.setImageResource(R.drawable.heartoutline);
                    showingFirst[0] = true;
                }

            }
        });

        holder.homeimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                lastTouchTime[0] = currentTouchTime[0];
                currentTouchTime[0] = System.currentTimeMillis();

                String likes = String.valueOf(holder.nooflikes.getText());
                String numberOnly= likes.replaceAll("[^0-9]", "");
                int nooflikesint1 = Integer.parseInt(numberOnly);

                if (currentTouchTime[0] - lastTouchTime[0] < 250) {
                    lastTouchTime[0] = 0;
                    currentTouchTime[0] = 0;
                    holder.nooflikes.setText(String.valueOf(nooflikesint1)+" likes");
                    holder.btnLike.setImageResource(R.drawable.redheart);
                }
            }
        });

        holder.nooflikes.setText(String.valueOf(getRandomNumberInRange(0,200))+" likes");
        holder.homename.setText(mDataset.get(position).getUploadedby());
        holder.homelocation.setText(mDataset.get(position).getLocation());
        holder.homecaption.setText(mDataset.get(position).getPhotocaption());
        holder.hometime.setText(DateUtils.getRelativeTimeSpanString(Long.parseLong(mDataset.get(position).getCreated_at())));
        holder.mTextHashTagHelper = HashTagHelper.Creator.create(holder.itemView.getResources().getColor(R.color.text_like_counter), null);
        holder.mTextHashTagHelper.handle(holder.homecaption);

        Context context = holder.homepropic.getContext();
        Context context1 = holder.homeimage.getContext();

        String propicurl = BASE_URL+"getpropic/"+mDataset.get(position).getPropicfilename();
        String homeimageurl = BASE_URL+"getparticularpic/"+mDataset.get(position).getPhotofilename();


        Glide.with(context).load(propicurl).into(holder.homepropic);
        Glide.with(context1).load(homeimageurl).into(holder.homeimage);
//        Picasso.with(context).load(propicurl).into(holder.homepropic);
//        Picasso.with(context1).load(homeimageurl).into(holder.homeimage);

        holder.btnMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popup = new PopupMenu(view.getContext(),view);
                MenuInflater inflater = popup.getMenuInflater();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        if(item.getTitle().equals("Share Image")){
                            Intent shareIntent = new Intent(Intent.ACTION_SEND);
                            shareIntent.setType("text/plain");
                            shareIntent.putExtra(Intent.EXTRA_TEXT,homeimageurl);
                            shareIntent.putExtra(Intent.EXTRA_SUBJECT, "The title");
                            view.getContext().startActivity(Intent.createChooser(shareIntent, "Share via..."));
                        }
                        return false;
                    }
                });
                inflater.inflate(R.menu.bottom_menu, popup.getMenu());
                popup.show();
            }
        });

        holder.homename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(view.getContext(),ViewUser.class);
                intent.putExtra("username",mDataset.get(position).getUploadedbyUsername());
                view.getContext().startActivity(intent);

            }
        });

    }

    public void addItem(DataParser dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public interface MyClickListener {
        public void onItemClick(int position, View v);
    }

    private static int getRandomNumberInRange(int min, int max) {

        if (min >= max) {
            throw new IllegalArgumentException("max must be greater than min");
        }

        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }

}
