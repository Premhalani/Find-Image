package com.example.prem.findimage.dataobjects;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Prem on 09-Mar-18.
 */

/**
 * Custom object that represents each image.
 * Also works as a entity to Room Database
 */
public class Image  implements Parcelable {
    private String id;
    private String title;
    private String description;
    private String url;

    public Image(String id,String title,String description, String url){
        this.id = id;
        this.description=description;
        this.title = title;
        this.url = url;
    }
    public Image(){

    }

    protected Image(Parcel in) {
        id = in.readString();
        title = in.readString();
        description = in.readString();
        url = in.readString();
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(description);
        parcel.writeString(url);
    }
}
