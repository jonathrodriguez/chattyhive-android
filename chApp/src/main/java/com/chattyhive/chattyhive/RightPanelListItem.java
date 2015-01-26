package com.chattyhive.chattyhive;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by J.Guzmán on 24/09/2014.
 */
public class RightPanelListItem {
    public String string;
    public final List<String> children = new ArrayList<String>();

    public RightPanelListItem(String string) {
        this.string = string;
    }
}
