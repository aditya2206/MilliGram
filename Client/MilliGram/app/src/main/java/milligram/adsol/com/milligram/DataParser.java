package milligram.adsol.com.milligram;

/**
 * Created by adityasarma on 26/02/18.
 */

public class DataParser {

    private String photofilename;
    private String photocaption;
    private String location;
    private String uploadedby;
    private String propicfilename;
    private String created_at;
    private String uploadedbyUsername;

    DataParser(String photofilename, String photocaption, String location, String uploadedby, String propicfilename, String created_at, String uploadedbyUsername){

        this.photofilename = photofilename;
        this.photocaption = photocaption;
        this.location = location;
        this.uploadedby = uploadedby;
        this.propicfilename = propicfilename;
        this.created_at = created_at;
        this.uploadedbyUsername = uploadedbyUsername;

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

    public String getUploadedby(){
        return uploadedby;
    }

    public String getPropicfilename(){
        return propicfilename;
    }

    public String getCreated_at(){
        return created_at;
    }

    public String getUploadedbyUsername() { return uploadedbyUsername; }

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

    public void setCreated_at(String created_at){
        this.created_at = created_at;
    }

    public void setUploadedbyUsername(String uploadedbyUsername){
        this.uploadedbyUsername = uploadedbyUsername;
    }


}
