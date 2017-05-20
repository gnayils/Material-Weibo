package com.gnayils.obiew.weibo.bean;

import java.io.Serializable;

/**
 * Created by Gnayils on 19/03/2017.
 */

public class AccessToken implements Serializable{

    public String uid;
    public String access_token;
    public long expires_in;
    public long remind_in;
    public String scope;

}
