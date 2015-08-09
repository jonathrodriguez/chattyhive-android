package com.chattyhive.Core.BusinessObjects.Home;

import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.BusinessObjects.Chats.Messages.Message;
import com.chattyhive.Core.BusinessObjects.Hives.HiveList;
import com.chattyhive.Core.BusinessObjects.Home.Cards.HiveMessageCard;
import com.chattyhive.Core.BusinessObjects.Users.User;
import com.chattyhive.Core.Util.Events.Event;
import com.chattyhive.Core.Util.Events.EventArgs;
import com.chattyhive.Core.Util.Events.EventHandler;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Jonathan on 30/05/2015.
 */
public class Home implements Collection<HomeCard> {

    /*********************/
    // HARDCODED variables
    private static int estimatedJoinDays = 7; //Number of days in the past for unknown join dates.
    private static int lastActivityPriorityDays = 7; //Number of days since last activity from the user to prioritize cards.
    private static int numberOfPrioritizedCards = 4; //Number of prioritized cards. Those will show up in home.
    /********************/

    private List<HomeCard> homeCards = null;
    private HiveList hiveList = null;
    private User owner;

    public Event<EventArgs> HomeReceived;

    public Home(User owner) {
        this.hiveList = owner.getSubscribedHives();
        this.owner = owner;
        this.HomeReceived = new Event<EventArgs>();

        this.hiveList.HiveListChanged.add(new EventHandler<EventArgs>(this, "onHiveListChanged", EventArgs.class));
    }


    public ArrayList<HomeCard> getHomeCards() {
        ArrayList<HomeCard> result = new ArrayList<HomeCard>();

        if (homeCards != null)
            result.addAll(this.homeCards);

        return result;
    }

    public void RequestHome() {
        //TODO: Request home to the server when implemented.

        new Thread() {
            @Override
            public void run() {
                if (homeCards != null)
                    homeCards.clear();
                else
                    homeCards = new ArrayList<HomeCard>();

                Message message;
                Message sentMessage;
                HiveMessageCard homeCard;
                TreeMap<Date,HiveMessageCard> lastActivity = new TreeMap<Date, HiveMessageCard>();
                GregorianCalendar joinDate = new GregorianCalendar();
                joinDate.add(Calendar.DATE,-1*estimatedJoinDays); //HARDCODED: "Estimated" join date to hive.
                GregorianCalendar lastPriority = new GregorianCalendar();
                lastPriority.add(Calendar.DATE,-1*lastActivityPriorityDays); // HARDCODED: Days to prioritize last activity.
                for (Hive hive : hiveList) {
                    if ((hive != null) && (hive.getPublicChat() != null) && (hive.getPublicChat().getConversation() != null) && (hive.getPublicChat().getConversation().getCount() > 0)) {
                        message = hive.getPublicChat().getConversation().getLastMessage();
                        if (message.getUser().getUserID().equals(owner.getUserID()))
                            sentMessage = message;
                        else
                            sentMessage = hive.getPublicChat().getConversation().getLastSentMessage(owner);

                        Date lastHiveActivity = ((sentMessage != null)?sentMessage.getOrdinationTimeStamp():joinDate.getTime());

                        homeCard = new HiveMessageCard(message);
                        homeCards.add(homeCard);

                        if (lastHiveActivity.after(lastPriority.getTime()))
                            lastActivity.put(lastHiveActivity,homeCard);
                    }
                }

                int end = ((lastActivity.size() < numberOfPrioritizedCards)?lastActivity.size():numberOfPrioritizedCards) -1; // HARDCODED: Number of hives to prioritize.

                HiveMessageCard[] hiveMessageCards = lastActivity.values().toArray(new HiveMessageCard[lastActivity.size()]);
                for (int i = end; i >= 0; i--)
                    hiveMessageCards[i].setPriorized(true);

                Collections.sort(homeCards,new HomeCardComparator());

                HomeReceived.fire(this, EventArgs.Empty());
            }
        }.start();
    }

    public void onHiveListChanged (Object sender, EventArgs eventArgs) {
        if ((sender == null) || ((sender instanceof Hive) && (((Hive) sender).getPublicChat() != null) && (((Hive) sender).getPublicChat().getConversation() != null) && (((Hive) sender).getPublicChat().getConversation().getCount() > 0))) {
            this.RequestHome();
        }
    }

    /**
     * Returns the number of elements in this collection.  If this collection
     * contains more than <tt>Integer.MAX_VALUE</tt> elements, returns
     * <tt>Integer.MAX_VALUE</tt>.
     *
     * @return the number of elements in this collection
     */
    @Override
    public int size() {
        return this.homeCards.size();
    }

    /**
     * Returns <tt>true</tt> if this collection contains no elements.
     *
     * @return <tt>true</tt> if this collection contains no elements
     */
    @Override
    public boolean isEmpty() {
        return this.homeCards.isEmpty();
    }

    /**
     * Returns <tt>true</tt> if this collection contains the specified element.
     * More formally, returns <tt>true</tt> if and only if this collection
     * contains at least one element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>.
     *
     * @param o element whose presence in this collection is to be tested
     * @return <tt>true</tt> if this collection contains the specified
     * element
     * @throws ClassCastException   if the type of the specified element
     *                              is incompatible with this collection
     *                              (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified element is null and this
     *                              collection does not permit null elements
     *                              (<a href="#optional-restrictions">optional</a>)
     */
    @Override
    public boolean contains(Object o) {
        return false;
    }

    /**
     * Returns an iterator over the elements in this collection.  There are no
     * guarantees concerning the order in which the elements are returned
     * (unless this collection is an instance of some class that provides a
     * guarantee).
     *
     * @return an <tt>Iterator</tt> over the elements in this collection
     */
    @Override
    public Iterator<HomeCard> iterator() {
        ArrayList<HomeCard> result = new ArrayList<HomeCard>();

        if (homeCards != null)
            result.addAll(this.homeCards);

        return result.iterator();
    }

    /**
     * Returns an array containing all of the elements in this collection.
     * If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     * <p/>
     * <p>The returned array will be "safe" in that no references to it are
     * maintained by this collection.  (In other words, this method must
     * allocate a new array even if this collection is backed by an array).
     * The caller is thus free to modify the returned array.
     * <p/>
     * <p>This method acts as bridge between array-based and collection-based
     * APIs.
     *
     * @return an array containing all of the elements in this collection
     */
    @Override
    public Object[] toArray() {
        return this.homeCards.toArray();
    }

    /**
     * Returns an array containing all of the elements in this collection;
     * the runtime type of the returned array is that of the specified array.
     * If the collection fits in the specified array, it is returned therein.
     * Otherwise, a new array is allocated with the runtime type of the
     * specified array and the size of this collection.
     * <p/>
     * <p>If this collection fits in the specified array with room to spare
     * (i.e., the array has more elements than this collection), the element
     * in the array immediately following the end of the collection is set to
     * <tt>null</tt>.  (This is useful in determining the length of this
     * collection <i>only</i> if the caller knows that this collection does
     * not contain any <tt>null</tt> elements.)
     * <p/>
     * <p>If this collection makes any guarantees as to what order its elements
     * are returned by its iterator, this method must return the elements in
     * the same order.
     * <p/>
     * <p>Like the {@link #toArray()} method, this method acts as bridge between
     * array-based and collection-based APIs.  Further, this method allows
     * precise control over the runtime type of the output array, and may,
     * under certain circumstances, be used to save allocation costs.
     * <p/>
     * <p>Suppose <tt>x</tt> is a collection known to contain only strings.
     * The following code can be used to dump the collection into a newly
     * allocated array of <tt>String</tt>:
     * <p/>
     * <pre>
     *     String[] y = x.toArray(new String[0]);</pre>
     *
     * Note that <tt>toArray(new Object[0])</tt> is identical in function to
     * <tt>toArray()</tt>.
     *
     * @param a the array into which the elements of this collection are to be
     *          stored, if it is big enough; otherwise, a new array of the same
     *          runtime type is allocated for this purpose.
     * @return an array containing all of the elements in this collection
     * @throws ArrayStoreException  if the runtime type of the specified array
     *                              is not a supertype of the runtime type of every element in
     *                              this collection
     * @throws NullPointerException if the specified array is null
     */
    @Override
    public <T> T[] toArray(T[] a) {
        return this.homeCards.toArray(a);
    }

    /**
     * Ensures that this collection contains the specified element (optional
     * operation).  Returns <tt>true</tt> if this collection changed as a
     * result of the call.  (Returns <tt>false</tt> if this collection does
     * not permit duplicates and already contains the specified element.)<p>
     * <p/>
     * Collections that support this operation may place limitations on what
     * elements may be added to this collection.  In particular, some
     * collections will refuse to add <tt>null</tt> elements, and others will
     * impose restrictions on the type of elements that may be added.
     * Collection classes should clearly specify in their documentation any
     * restrictions on what elements may be added.<p>
     * <p/>
     * If a collection refuses to add a particular element for any reason
     * other than that it already contains the element, it <i>must</i> throw
     * an exception (rather than returning <tt>false</tt>).  This preserves
     * the invariant that a collection always contains the specified element
     * after this call returns.
     *
     * @param homeCard element whose presence in this collection is to be ensured
     * @return <tt>true</tt> if this collection changed as a result of the
     * call
     * @throws UnsupportedOperationException if the <tt>add</tt> operation
     *                                       is not supported by this collection
     * @throws ClassCastException            if the class of the specified element
     *                                       prevents it from being added to this collection
     * @throws NullPointerException          if the specified element is null and this
     *                                       collection does not permit null elements
     * @throws IllegalArgumentException      if some property of the element
     *                                       prevents it from being added to this collection
     * @throws IllegalStateException         if the element cannot be added at this
     *                                       time due to insertion restrictions
     */
    @Override
    public boolean add(HomeCard homeCard) {
        return false;
    }

    /**
     * Removes a single instance of the specified element from this
     * collection, if it is present (optional operation).  More formally,
     * removes an element <tt>e</tt> such that
     * <tt>(o==null&nbsp;?&nbsp;e==null&nbsp;:&nbsp;o.equals(e))</tt>, if
     * this collection contains one or more such elements.  Returns
     * <tt>true</tt> if this collection contained the specified element (or
     * equivalently, if this collection changed as a result of the call).
     *
     * @param o element to be removed from this collection, if present
     * @return <tt>true</tt> if an element was removed as a result of this call
     * @throws ClassCastException            if the type of the specified element
     *                                       is incompatible with this collection
     *                                       (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException          if the specified element is null and this
     *                                       collection does not permit null elements
     *                                       (<a href="#optional-restrictions">optional</a>)
     * @throws UnsupportedOperationException if the <tt>remove</tt> operation
     *                                       is not supported by this collection
     */
    @Override
    public boolean remove(Object o) {
        return false;
    }


    /**
     * Returns <tt>true</tt> if this collection contains all of the elements
     * in the specified collection.
     *
     * @param c collection to be checked for containment in this collection
     * @return <tt>true</tt> if this collection contains all of the elements
     * in the specified collection
     * @throws ClassCastException   if the types of one or more elements
     *                              in the specified collection are incompatible with this
     *                              collection
     *                              (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException if the specified collection contains one
     *                              or more null elements and this collection does not permit null
     *                              elements
     *                              (<a href="#optional-restrictions">optional</a>),
     *                              or if the specified collection is null.
     * @see #contains(Object)
     */
    @Override
    public boolean containsAll(Collection<?> c) {
        return false;
    }

    /**
     * Adds all of the elements in the specified collection to this collection
     * (optional operation).  The behavior of this operation is undefined if
     * the specified collection is modified while the operation is in progress.
     * (This implies that the behavior of this call is undefined if the
     * specified collection is this collection, and this collection is
     * nonempty.)
     *
     * @param c collection containing elements to be added to this collection
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>addAll</tt> operation
     *                                       is not supported by this collection
     * @throws ClassCastException            if the class of an element of the specified
     *                                       collection prevents it from being added to this collection
     * @throws NullPointerException          if the specified collection contains a
     *                                       null element and this collection does not permit null elements,
     *                                       or if the specified collection is null
     * @throws IllegalArgumentException      if some property of an element of the
     *                                       specified collection prevents it from being added to this
     *                                       collection
     * @throws IllegalStateException         if not all the elements can be added at
     *                                       this time due to insertion restrictions
     * @see #add(Object)
     */
    @Override
    public boolean addAll(Collection<? extends HomeCard> c) {
        return false;
    }

    /**
     * Removes all of this collection's elements that are also contained in the
     * specified collection (optional operation).  After this call returns,
     * this collection will contain no elements in common with the specified
     * collection.
     *
     * @param c collection containing elements to be removed from this collection
     * @return <tt>true</tt> if this collection changed as a result of the
     * call
     * @throws UnsupportedOperationException if the <tt>removeAll</tt> method
     *                                       is not supported by this collection
     * @throws ClassCastException            if the types of one or more elements
     *                                       in this collection are incompatible with the specified
     *                                       collection
     *                                       (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException          if this collection contains one or more
     *                                       null elements and the specified collection does not support
     *                                       null elements
     *                                       (<a href="#optional-restrictions">optional</a>),
     *                                       or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean removeAll(Collection<?> c) {
        return false;
    }

    /**
     * Retains only the elements in this collection that are contained in the
     * specified collection (optional operation).  In other words, removes from
     * this collection all of its elements that are not contained in the
     * specified collection.
     *
     * @param c collection containing elements to be retained in this collection
     * @return <tt>true</tt> if this collection changed as a result of the call
     * @throws UnsupportedOperationException if the <tt>retainAll</tt> operation
     *                                       is not supported by this collection
     * @throws ClassCastException            if the types of one or more elements
     *                                       in this collection are incompatible with the specified
     *                                       collection
     *                                       (<a href="#optional-restrictions">optional</a>)
     * @throws NullPointerException          if this collection contains one or more
     *                                       null elements and the specified collection does not permit null
     *                                       elements
     *                                       (<a href="#optional-restrictions">optional</a>),
     *                                       or if the specified collection is null
     * @see #remove(Object)
     * @see #contains(Object)
     */
    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    /**
     * Removes all of the elements from this collection (optional operation).
     * The collection will be empty after this method returns.
     *
     * @throws UnsupportedOperationException if the <tt>clear</tt> operation
     *                                       is not supported by this collection
     */
    @Override
    public void clear() {
        this.homeCards.clear();
    }

    public class HomeCardComparator implements Comparator<HomeCard> {
        @Override
        public int compare(HomeCard o1, HomeCard o2) {
            if ((o1 == null) && (o2 != null))
                return 1;
            else if ((o1 != null) && (o2 == null))
                return -1;
            else if (o1 == null) //&& (o2 == null)) <- Which is always true
                return 0;
            else if ((o1 instanceof HiveMessageCard) && (!(o2 instanceof HiveMessageCard)))
                return 1;
            else if ((!(o1 instanceof HiveMessageCard)) && (o2 instanceof HiveMessageCard))
                return -1;
            else if (!(o1 instanceof HiveMessageCard)) //&& (!(o2 instanceof HiveMessageCard))) <- Which is always true
                return 0;
            else {
                HiveMessageCard h1 = (HiveMessageCard)o1;
                HiveMessageCard h2 = (HiveMessageCard)o2;

                if ((!h1.getPriorized()) && (h2.getPriorized()))
                    return 1;
                else if ((h1.getPriorized()) && (!h2.getPriorized()))
                    return -1;
                else
                    return h2.getMessage().getOrdinationTimeStamp().compareTo(h1.getMessage().getOrdinationTimeStamp());
            }
        }
    }
}
