package com.sales.swiftgassales;

import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class ActiveShop {

    private String image,shopName,shopNo,vendorName;
    private Date timestamp;

    public ActiveShop() {
        //empty
    }

    public ActiveShop(String image, String shopName, String shopNo, String vendorName, Date timestamp) {
        this.image = image;
        this.shopName = shopName;
        this.shopNo = shopNo;
        this.vendorName = vendorName;
        this.timestamp = timestamp;
    }


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getShopName() {
        return shopName;
    }

    public void setShopName(String shopName) {
        this.shopName = shopName;
    }

    public String getShopNo() {
        return shopNo;
    }

    public void setShopNo(String shopNo) {
        this.shopNo = shopNo;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}

