package com.example.practica3eduardogomez;

public class Contact {

    String id, image, name, lastName, email, address, phoneNumber, addedTime, updateTime, appointment;

    //Is always a good practice, to create an empty constructor

    //C#, witch is the improved version of JAVA lol, the create and empty or default constructor
    //By the way c# is my favorite language, XAMARIN IS THE FUTURE

    public Contact() {
    }

    public Contact(String image, String name, String lastName, String email, String address, String phoneNumber, String addedTime, String updateTime, String appointment) {
        this.image = image;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.addedTime = addedTime;
        this.updateTime = updateTime;
        this.appointment = appointment;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getAppointment() {
        return appointment;
    }

    public void setAppointment(String appointment) {
        this.appointment = appointment;
    }
}