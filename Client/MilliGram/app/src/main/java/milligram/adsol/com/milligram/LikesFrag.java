package milligram.adsol.com.milligram;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by adityasarma on 06/02/18.
 */

public class LikesFrag extends android.app.Fragment {
    public static LikesFrag newInstance() {
        LikesFrag fragment = new LikesFrag();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.likes_frag, container, false);
    }
}
