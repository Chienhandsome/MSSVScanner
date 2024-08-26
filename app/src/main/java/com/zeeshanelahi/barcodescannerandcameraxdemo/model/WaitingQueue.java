package com.zeeshanelahi.barcodescannerandcameraxdemo.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;

public class WaitingQueue {
    private static WaitingQueue instance;
    private Queue<String> queue;
    private WaitingQueue() {
    }
    public static synchronized WaitingQueue getInstance() {
        if (instance == null) {
            instance = new WaitingQueue();
        }
        return instance;
    }


}
