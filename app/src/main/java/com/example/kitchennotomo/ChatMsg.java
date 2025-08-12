package com.example.kitchennotomo;

public class ChatMsg {
    public static final int USER = 0;
    public static final int BOT  = 1;
    public final int role;
    public final String text;

    public ChatMsg(int role, String text) {
        this.role = role;
        this.text = text;
    }
}
