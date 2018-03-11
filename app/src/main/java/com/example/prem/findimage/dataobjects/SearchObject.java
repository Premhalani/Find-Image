package com.example.prem.findimage.dataobjects;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.support.annotation.NonNull;

import com.arlib.floatingsearchview.suggestions.model.SearchSuggestion;

/**
 * Created by Prem on 09-Mar-18.
 */
@Entity
public class SearchObject implements SearchSuggestion{
    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "searchText")
    private String searchText;
    @ColumnInfo(name = "isHistory")
    private boolean isHistory = true;

    public SearchObject(String searchText){
        this.searchText = searchText;
    }
    public SearchObject(Parcel parcel){
        this.searchText = parcel.readString();
        this.isHistory = parcel.readInt() != 0;
    }
    public void setHistory(boolean history) {
        isHistory = history;
    }
    public boolean getHistory() {
        return isHistory;
    }

    public String getSearchText() {
        return searchText;
    }
    public static final Creator<SearchObject> CREATOR = new Creator<SearchObject>() {

        @Override
        public SearchObject createFromParcel(Parcel parcel) {
            return new SearchObject(parcel);
        }

        @Override
        public SearchObject[] newArray(int i) {
            return new SearchObject[i];
        }
    };
    public void setSearchText(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public String getBody() {
        return searchText;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(searchText);
        parcel.writeInt(isHistory ? 1 : 0);
    }
}
