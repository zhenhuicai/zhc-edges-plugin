package com.zhm.edges.plugins.api.job.vo;

public class VendorProfile {

    protected String accountId;

    protected String vendorId;

    protected Boolean defaultProfile;

    protected String profileName;

    protected String dir;

    public String getAccountId() {
        return accountId;
    }

    public VendorProfile setAccountId(String accountId) {
        this.accountId = accountId;
        return this;
    }

    public String getVendorId() {
        return vendorId;
    }

    public VendorProfile setVendorId(String vendorId) {
        this.vendorId = vendorId;
        return this;
    }

    public Boolean getDefaultProfile() {
        return defaultProfile;
    }

    public VendorProfile setDefaultProfile(Boolean defaultProfile) {
        this.defaultProfile = defaultProfile;
        return this;
    }

    public String getProfileName() {
        return profileName;
    }

    public VendorProfile setProfileName(String profileName) {
        this.profileName = profileName;
        return this;
    }

    public String getDir() {
        return dir;
    }

    public VendorProfile setDir(String dir) {
        this.dir = dir;
        return this;
    }
}
