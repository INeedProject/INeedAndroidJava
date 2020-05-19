package com.ineed.senior;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Message implements Comparable<Message> {
    private String mSender;

    private String mMessage;
    private String mReceiver;

    private Long timestamp;

    public Message(){
    }

    public Message(String mSender, String mMessage, String mReceiver, Long timestamp) {
        this.mSender = mSender;
        this.mMessage = mMessage;
        this.mReceiver = mReceiver;
        this.timestamp = timestamp;
    }

    public Message(String mSender, String mMessage, String mReceiver) {
        this.mSender = mSender;
        this.mMessage = mMessage;
        this.mReceiver = mReceiver;
    }

    public String getmSender() {
        return mSender;
    }

    public String getmMessage() {
        return mMessage;
    }

    public String getmReceiver() {
        return mReceiver;
    }

    public Long getTimestamp() { return timestamp; }

    @Override
    public int compareTo(Message o) {
        return this.getTimestamp().compareTo(o.getTimestamp());
    }
}
