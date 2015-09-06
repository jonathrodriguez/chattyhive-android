package com.chattyhive.Core.BusinessObjects.Hives;

import com.chattyhive.Core.BusinessObjects.Users.User;

/**
 * Created by jonathan on 11/08/2015.
 */
public class HiveSubscription {
    private Hive hive;
    private User user;

    public HiveSubscription(Hive hive, User user) {
        this.hive = hive;
        this.user = user;
    }

    public Hive getHive() {
        return this.hive;
    }
    public void setHive(Hive hive) {
        this.hive = hive;
    }

    public User getUser() {
        return this.user;
    }
    public void setUser(User user) {
        this.user = user;
    }
}
