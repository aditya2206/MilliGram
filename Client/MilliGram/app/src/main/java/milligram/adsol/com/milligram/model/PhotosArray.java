package milligram.adsol.com.milligram.model;

import java.util.ArrayList;

/**
 * Created by adityasarma on 04/03/18.
 */

public class PhotosArray {

    private ArrayList<String> photoarray;
    private int noofposts;

    public void setPhotoarray(ArrayList<String> photoarray){
        this.photoarray = photoarray;
    }

    public ArrayList<String> getPhotoarray(){
        return photoarray;
    }

    public void setNoofposts(int noofposts){
        this.noofposts = noofposts;
    }

    public int getNoofposts(){
        return noofposts;
    }

}
