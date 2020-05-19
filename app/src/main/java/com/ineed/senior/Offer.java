package com.ineed.senior;

public class Offer {

    String email, needhash, offerer;
    boolean state;

    public Offer() {
    }

    public Offer(String email, String needhash, String offerer, boolean state) {
        this.email = email;
        this.needhash = needhash;
        this.offerer = offerer;
        this.state = state;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNeedhash() {
        return needhash;
    }

    public void setNeedhash(String needhash) {
        this.needhash = needhash;
    }

    public String getOfferer() {
        return offerer;
    }

    public void setOfferer(String offerer) {
        this.offerer = offerer;
    }

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}
