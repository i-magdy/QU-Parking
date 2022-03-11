package com.example.quparking.model;

import java.io.Serializable;

public class UserModel implements Serializable {


    private String userName;
    private String email;
    private String phone;
    private String password;
    private String role;
    private String serialNo;
    private String key;
    private boolean payment;
    private String holderName;
    private String cardNumber;
    private String zipcode;
    public UserModel(){

    }

    public UserModel(String key ,String userName, String email, String phone, String password, String role, String serialNo,boolean payment,String holderName,String cardNumber,String zipcode) {
        this.key = key;
        this.userName = userName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.role = role;
        this.serialNo = serialNo;
        this.payment = payment;
        this.holderName = holderName;
        this.cardNumber = cardNumber;
        this.zipcode = zipcode;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public String getPhone() {
        return phone;
    }

    public String getSerialNo() {
        return serialNo;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getRole() {
        return role;
    }

    public boolean isPayment() {
        return payment;
    }

    public void setPayment(boolean payment) {
        this.payment = payment;
    }

    public String getHolderName() {
        return holderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getZipcode() {
        return zipcode;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public void setZipcode(String zipcode) {
        this.zipcode = zipcode;
    }
}
