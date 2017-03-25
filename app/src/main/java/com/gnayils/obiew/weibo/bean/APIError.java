package com.gnayils.obiew.weibo.bean;

/**
 * Created by Gnayils on 25/03/2017.
 */

public class APIError {

    public String error;
    public int error_code;
    public String request;

    public static final int INVALID_ACCESS_TOKEN = 21332;
    public static final int EXPIRED_TOKEN = 21327;

}
