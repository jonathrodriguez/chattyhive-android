package com.chattyhive.backend.businessobjects;

import com.chattyhive.backend.Controller;
import com.chattyhive.backend.StaticParameters;
import com.chattyhive.backend.businessobjects.Chats.Hive;
import com.chattyhive.backend.contentprovider.DataProvider;
import com.chattyhive.backend.contentprovider.formats.EXPLORE_FILTER;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.contentprovider.formats.HIVE_LIST;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.Event;
import com.chattyhive.backend.util.events.EventArgs;
import com.chattyhive.backend.util.events.EventHandler;

import java.util.ArrayList;

/**
 * Created by Jonathan on 14/12/2014.
 */

public class Explore {
    public static enum SortType {
        OUTSTANDING,
        USERS,
        CREATION_DATE,
        TRENDING
    }

    private Boolean hasMore;
    private Boolean loadingMore;
    public Boolean HasMore() {
        return this.hasMore;
    }

    private int nextStartIndex;

    private SortType sortType;
    public SortType getSortType() {
        return this.sortType;
    }

    private ArrayList<Hive> results;
    public ArrayList<Hive> getResults() {
        return this.results;
    }

    public Event<EventArgs> onMoreResults;

    private Controller controller;
    private DataProvider dataProvider;

    public Explore(Controller controller, SortType sortType) {
        this.hasMore = true;
        this.loadingMore = false;
        this.nextStartIndex = 0;
        this.sortType = sortType;
        this.results = new ArrayList<Hive>();
        this.controller = controller;
        this.dataProvider = this.controller.getDataProvider();

        this.onMoreResults = new Event<EventArgs>();
    }

    public void More() {
        if ((!hasMore) || (loadingMore)) return;

        this.loadingMore = true;
        int howMany = ((this.nextStartIndex == 0)? StaticParameters.ExploreStart : StaticParameters.ExploreCount);

        this.dataProvider.ExploreHives(nextStartIndex,howMany,sortType,new EventHandler<CommandCallbackEventArgs>(this,"onExploreHivesCallback",CommandCallbackEventArgs.class));
    }

    public void onExploreHivesCallback (Object sender, CommandCallbackEventArgs eventArgs) {
        ArrayList<Format> receivedFormats = eventArgs.getReceivedFormats();
        ArrayList<Format> sentFormats = eventArgs.getSentFormats();

        SortType sortType = null;

        int expectedNextStart = nextStartIndex;

        for (Format format : sentFormats)
            if (format instanceof EXPLORE_FILTER) {
                if (((EXPLORE_FILTER) format).TYPE.equalsIgnoreCase(SortType.OUTSTANDING.toString()))
                    sortType = SortType.OUTSTANDING;
                else if (((EXPLORE_FILTER) format).TYPE.equalsIgnoreCase(SortType.USERS.toString()))
                    sortType = SortType.USERS;
                else if (((EXPLORE_FILTER) format).TYPE.equalsIgnoreCase(SortType.TRENDING.toString()))
                    sortType = SortType.TRENDING;
                else if (((EXPLORE_FILTER) format).TYPE.equalsIgnoreCase(SortType.CREATION_DATE.toString()))
                    sortType = SortType.CREATION_DATE;

                expectedNextStart += (((EXPLORE_FILTER) format).RESULT_INTERVAL.COUNT);
            }

        if (sortType != this.sortType) return;

        Boolean resultsChanged = false;

        for (Format format : receivedFormats)
            if (format instanceof HIVE) {
                resultsChanged = resultsChanged || this.results.add(new Hive((HIVE) format));
                this.nextStartIndex++;
            }
            else if (format instanceof HIVE_LIST)
                for (HIVE hive : ((HIVE_LIST) format).LIST) {
                    resultsChanged = this.results.add(new Hive(hive)) || resultsChanged;
                    this.nextStartIndex++;
                }

        this.hasMore = (this.nextStartIndex == expectedNextStart);

        if ((this.onMoreResults != null) && (resultsChanged))
            this.onMoreResults.fire(this,EventArgs.Empty());

        this.loadingMore = false;
    }
}
