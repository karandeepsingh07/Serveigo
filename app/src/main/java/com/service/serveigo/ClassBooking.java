package com.service.serveigo;

public class ClassBooking {
    public String services;
    public String comments;
    public String date;
    public String time;
    public String vendorType;
    public String vendorName;
    public String vendorID;
    public String status;
    public String jobId;
    public String vendorImage;
    public String amount;
    public String vendorNumber;

    public String getServices() {
        return services;
    }

    public String getComments() {
        return comments;
    }

    public String getDate() {
        return date;
    }

    public String getTime() {
        return time;
    }

    public String getVendorType() {
        return vendorType;
    }

    public String getVendorName() {
        return vendorName;
    }

    public String getVendorID() {
        return vendorID;
    }

    public String getStatus() {
        return status;
    }

    public String getJobId() {
        return jobId;
    }

    public void setJobId(String jobId) {
        this.jobId = jobId;
    }

    public String getVendorImage() {
        return vendorImage;
    }

    public String getAmount() { return amount; }

    public void setVendorType(String vendorType) {
        this.vendorType = vendorType;
    }

    public String getVendorNumber() {
        return vendorNumber;
    }
}
