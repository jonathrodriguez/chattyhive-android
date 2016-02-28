package com.chattyhive.Core.ContentProvider.Formats;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;


/**
 * ResponseRequestInfo
 * <p>
 * Response body for the Request Info method.
 *
 */
@Generated("org.jsonschema2pojo")
public class RES_REQUEST_INFO {

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
     * Request date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     */
    @SerializedName("creation_date")
    @Expose
    private String creationDate;
    /**
     * Response date, if responded. Example: 2015-05-13T15:54:50.315865Z
     *
     */
    @SerializedName("response_date")
    @Expose
    private String responseDate;

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

    /**
     * Request date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @return
     * The creationDate
     */
    public String getCreationDate() {
        return creationDate;
    }

    /**
     * Request date. Example: 2015-05-13T15:54:50.315865Z
     * (Required)
     *
     * @param creationDate
     * The creation_date
     */
    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    /**
     * Response date, if responded. Example: 2015-05-13T15:54:50.315865Z
     *
     * @return
     * The responseDate
     */
    public String getResponseDate() {
        return responseDate;
    }

    /**
     * Response date, if responded. Example: 2015-05-13T15:54:50.315865Z
     *
     * @param responseDate
     * The response_date
     */
    public void setResponseDate(String responseDate) {
        this.responseDate = responseDate;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(requestID).append(senderID).append(receiverID).append(requestStatus).append(creationDate).append(responseDate).toHashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if ((other instanceof RES_REQUEST_INFO) == false) {
            return false;
        }
        RES_REQUEST_INFO rhs = ((RES_REQUEST_INFO) other);
        return new EqualsBuilder().append(requestID, rhs.requestID).append(senderID, rhs.senderID).append(receiverID, rhs.receiverID).append(requestStatus, rhs.requestStatus).append(creationDate, rhs.creationDate).append(responseDate, rhs.responseDate).isEquals();
    }

}