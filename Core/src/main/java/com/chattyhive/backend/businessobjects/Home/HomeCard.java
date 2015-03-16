package com.chattyhive.backend.BusinessObjects.Home;

/**
 * Created by Jonathan on 07/10/2014.
 */
public abstract class HomeCard {
    protected HomeCardType cardType;
    public HomeCardType getCardType() {
        return this.cardType;
    }

    public HomeCard() {};
}
