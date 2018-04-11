package milligram.adsol.com.milligram.model;

/**
 * Created by adityasarma on 14/02/18.
 */

public class Photo {

    private String photofilename;
    private String photocaption;
    private String location;
    private String uploadedby;
    private String propicfilename;
    private String uploadedbyusername;

    public void setPhotofilename(String photofilename){
        this.photofilename = photofilename;
    }

    public void setPhotocaption(String photocaption){
        this.photocaption = photocaption;
    }

    public void setLocation(String location){
        this.location = location;
    }

    public void setUploadedby(String uploadedby){
        this.uploadedby = uploadedby;
    }

    public void setPropicfilename(String propicfilename){
        this.propicfilename = propicfilename;
    }

    public void setUploadedbyusername(String uploadedbyusername){
        this.uploadedbyusername = uploadedbyusername;
    }

    public String getPhotofilename(){
        return photofilename;
    }

    public String getPhotocaption(){
        return photocaption;
    }

    public String getLocation(){
        return location;
    }

    public  String getUploadedby(){
        return uploadedby;
    }

    public String getUploadedbyusername(){
        return uploadedbyusername;
    }

}
