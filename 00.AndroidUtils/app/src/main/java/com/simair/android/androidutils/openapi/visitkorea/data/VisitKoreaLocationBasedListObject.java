package com.simair.android.androidutils.openapi.visitkorea.data;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by simair on 17. 7. 18.
 */

public class VisitKoreaLocationBasedListObject implements Serializable {
    @SerializedName("contentid")        long contentId;
    @SerializedName("contenttypeid")    long contentTypeId;
    @SerializedName("title")            String title;
    @SerializedName("addr1")            String address;
    @SerializedName("firstimage")       String imageURL;
    @SerializedName("firstimage2")      String thumbnailURL;
    @SerializedName("tel")              String contactNumber;

    @SerializedName("addr2")            String address2;
    @SerializedName("dist")             String distance;        // 거리 [m]
    @SerializedName("mapx")             double longitude;
    @SerializedName("mapy")             double latitude;

    int totalCount;
    int pageNo;
    int numOfRows;

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getPageNo() {
        return pageNo;
    }

    public void setPageNo(int pageNo) {
        this.pageNo = pageNo;
    }

    public int getNumOfRows() {
        return numOfRows;
    }

    public void setNumOfRows(int numOfRows) {
        this.numOfRows = numOfRows;
    }

    public long getContentId() {
        return contentId;
    }

    public void setContentId(long contentId) {
        this.contentId = contentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public String getThumbnailURL() {
        return thumbnailURL;
    }

    public void setThumbnailURL(String thumbnailURL) {
        this.thumbnailURL = thumbnailURL;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public long getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(long contentTypeId) {
        this.contentTypeId = contentTypeId;
    }
}
