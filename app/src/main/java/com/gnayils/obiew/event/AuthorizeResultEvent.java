package com.gnayils.obiew.event;

/**
 * Created by Gnayils on 13/11/2016.
 */

public class AuthorizeResultEvent {

    public boolean isSuccessful;
    public boolean isCancelled;
    public boolean isFailed;
    public String failedCause;

    public AuthorizeResultEvent(boolean isSuccessful, boolean isCancelled, boolean isFailed, String failedCause) {
        this.isSuccessful = isSuccessful;
        this.isCancelled = isCancelled;
        this.isFailed = isFailed;
        this.failedCause = failedCause;
    }
}
