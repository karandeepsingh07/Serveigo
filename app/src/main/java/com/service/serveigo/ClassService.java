package com.service.serveigo;

public class ClassService {

    public String head;
    public boolean checked;
    public long price;
    public String imageUrl;
    public String uid;
    public String duration;
    public long discount;
    public boolean visible;

    public String getHead() {
        return head;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public long getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getUid() {
        return uid;
    }

    public long getDiscount() {
        return discount;
    }

    public String getDuration() {
        return duration;
    }
}
