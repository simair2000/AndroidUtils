package com.simair.android.androidutils.openapi.visitkorea.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 18.
 */

public class VisitKoreaDetailCommonObject implements Serializable {
    @SerializedName("contentid")        long contentId;
    @SerializedName("contenttypeid")    long contentTypeId;
    @SerializedName("createdtime")      String createdTime;
    @SerializedName("modifiedtime")     String modifiedTime;
    @SerializedName("firstimage")       String imgURL;
    @SerializedName("firstimage2")      String thumbnailURL;
    @SerializedName("homepage")         String homePage;
    @SerializedName("mapy")             double latitude;
    @SerializedName("mapx")             double longitude;
    @SerializedName("title")            String title;
    @SerializedName("zipcode")          String zipCode;
    @SerializedName("addr1")            String address1;
    @SerializedName("addr2")            String address2;
    @SerializedName("overview")         String overview;


    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public long getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(long contentTypeId) {
        this.contentTypeId = contentTypeId;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(String modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public String getImgURL() {
        return imgURL;
    }

    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getHomePage() {
        return homePage;
    }

    public void setHomePage(String homePage) {
        this.homePage = homePage;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }
}
