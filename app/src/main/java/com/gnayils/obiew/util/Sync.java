package com.gnayils.obiew.util;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

/**
 * Created by Gnayils on 28/05/2017.
 */

public class Sync {

    public static void wait(Object object) {
        synchronized (object) {
            try {
                object.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void notifyAll(Object object) {
        synchronized (object) {
            object.notifyAll();
        }
    }

    public static void notify(Object object) {
        synchronized (object) {
            object.notify();
        }
    }
}
