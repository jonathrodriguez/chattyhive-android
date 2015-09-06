package com.chattyhive.Core.BusinessObjects.Subscriptions;

import com.chattyhive.Core.BusinessObjects.Users.User;

import java.util.Date;

/**
 * Created by jonathrodriguez on 11/08/2015.
 */
public class Subscription<T extends ISubscribable> {

    private User user;
    private T subscribable;

    private Date creationDate;
    private String subscriptionState;

    private Boolean expelled;
    private Date expulsionDueDate;

    public Subscription() {

    }


    public User getUser() {
        return this.user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public T getSubscribable() {
        return this.subscribable;
    }
    public void setSubscribable(T subscribable) {
        this.subscribable = subscribable;
    }

    public Date getCreationDate() {
        return this.creationDate;
    }
    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public String getSubscriptionState() {
        return this.subscriptionState;
    }
    public void setSubscriptionState(String subscriptionState) {
        this.subscriptionState = subscriptionState;
    }

    public Boolean getExpelled() {
        return this.expelled;
    }
    public void setExpelled(Boolean expelled) {
        this.expelled = expelled;
    }

    public Date getExpulsionDueDate() {
        return this.expulsionDueDate;
    }
    public void setExpulsionDueDate(Date expulsionDueDate) {
        this.expulsionDueDate = expulsionDueDate;
    }
}
