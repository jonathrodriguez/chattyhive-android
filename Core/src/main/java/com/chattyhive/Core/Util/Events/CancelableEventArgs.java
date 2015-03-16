package com.chattyhive.Core.Util.Events;

/*
 * Created by Jonathan on 14/07/2014.
 */
public class CancelableEventArgs extends EventArgs {
    private Boolean canceled;

    public CancelableEventArgs() {
        super();
        this.canceled = false;
    }

    public Boolean isCanceled() {
        return this.canceled;
    }
    public void Abort() {
        this.canceled = true;
    }
}
