package com.example.firebasephotoupload;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Exclude;

public class PhotoUpload {
    private String fileName;
    private String imageUri;
    private String key;
    private String user;


    public PhotoUpload() {
    }

    public PhotoUpload(String fileName, String imageUri, String user) {
        if(fileName.trim().equals("")){
            fileName = "No name";
        }
        this.fileName = fileName;
        this.imageUri = imageUri;
        this.user = user;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setImageUri(String imageUri) {
        this.imageUri = imageUri;
    }

    @Exclude
    public void setKey(String key){
        this.key = key;
    }

    public String getFileName() {
        return fileName;
    }

    public String getImageUri() {
        return imageUri;
    }

    public String getUser() {
        return user;
    }

    @Exclude
    public String getKey(){
        return key;
    }
}
