package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * List of requests
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_REQUEST_LIST {

    /**
     * Identifier of the request.
     * (Required)
     *
     */
    @SerializedName("request_ID")
    @Expose
    private String requestID;
    /**
     * Public_name of the sender user.
     * (Required)
     *
     */
    @SerializedName("sender_ID")
    @Expose
    private String senderID;
    /**
     * Public_name of the receiver user.
     * (Required)
     *
     */
    @SerializedName("receiver_ID")
    @Expose
    private String receiverID;
    /**
     * Status of the request.
     * (Required)
     *
     */
    @SerializedName("request_status")
    @Expose
    private String requestStatus;

    /**
     * Identifier of the request.
     * (Required)
     *
     * @return
     * The requestID
     */
    public String getRequestID() {
        return requestID;
    }

    /**
     * Identifier of the request.
     * (Required)
     *
     * @param requestID
     * The request_ID
     */
    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    /**
     * Public_name of the sender user.
     * (Required)
     *
     * @return
     * The senderID
     */
    public String getSenderID() {
        return senderID;
    }

    /**
     * Public_name of the sender user.
     * (Required)
     *
     * @param senderID
     * The sender_ID
     */
    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    /**
     * Public_name of the receiver user.
     * (Required)
     *
     * @return
     * The receiverID
     */
    public String getReceiverID() {
        return receiverID;
    }

    /**
     * Public_name of the receiver user.
     * (Required)
     *
     * @param receiverID
     * The receiver_ID
     */
    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    /**
     * Status of the request.
     * (Required)
     *
     * @return
     * The requestStatus
     */
    public String getRequestStatus() {
        return requestStatus;
    }

    /**
     * Status of the request.
     * (Required)
     *
     * @param requestStatus
     * The request_status
     */
    public void setRequestStatus(String requestStatus) {
        this.requestStatus = requestStatus;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(requestID).append(senderID).append(receiverID).append(requestStatus).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_REQUEST_LIST) == false) {
            return false;
        }
        RES_REQUEST_LIST rhs = ((RES_REQUEST_LIST) other);
        return new EqualsBuilder().append(requestID, rhs.requestID).append(senderID, rhs.senderID).append(receiverID, rhs.receiverID).append(requestStatus, rhs.requestStatus).isEquals();
    }

}