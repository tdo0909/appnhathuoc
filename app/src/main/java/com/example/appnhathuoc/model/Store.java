package com.example.appnhathuoc.model;

import java.io.Serializable;

public class Store implements Serializable {
    private String id;
    private String name;
    private String address;
    private String province;
    private String phone;
    private String openingHours;

    public Store() {}

    public Store(String id, String name, String address, String province, String phone, String openingHours) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.province = province;
        this.phone = phone;
        this.openingHours = openingHours;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getAddress() { return address; }
    public String getProvince() { return province; }
    public String getPhone() { return phone; }
    public String getOpeningHours() { return openingHours; }
}