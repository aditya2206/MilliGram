package milligram.adsol.com.milligram.model;

/**
 * Created by adityasarma on 06/02/18.
 */

public class User {

    private String name;
    private String email;
    private String mobile;
    private String username;
    private String password;
    private String bio;
    private String propicfilename;
    private String verified;
    private String created_at;
    private String newPassword;
    private String token;

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setPropicfilename(String propicfilename) {
        this.propicfilename = propicfilename;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getMobile() {
        return mobile;
    }

    public String getUsername() {
        return username;
    }

    public String getBio() {
        return bio;
    }

    public String getPropicfilename() {
        return propicfilename;
    }

    public String getVerified() {
        return verified;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
