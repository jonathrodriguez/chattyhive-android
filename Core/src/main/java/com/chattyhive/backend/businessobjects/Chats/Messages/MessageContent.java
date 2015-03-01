package com.chattyhive.backend.businessobjects.Chats.Messages;

import com.chattyhive.backend.businessobjects.Image;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_CONTENT;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;

/**
 * Created by Jonathan on 11/12/13.
 * This class represents the message content.
 */
public class MessageContent {
    String text;
    String contentType;
    Image image;

    /**
     * Public constructor.
     * @param text a string representing the content of the message.
     */
    public MessageContent(String text) {
        this.text = text;
        this.contentType = "TEXT";
    }

    public MessageContent(String contentType, String text) {
        this.contentType = contentType;
        this.text = text;
    }

    /**
     * Public constructor.
     * @param jsonMessageContent a JSONElement which represents the message content.
     */
    public MessageContent (JsonElement jsonMessageContent) {
        this.fromJson(jsonMessageContent);
    }

    public MessageContent (Format format) {
        this.fromFormat(format);
    }
    /**
     * Sets the content of this message content object.
     * @param text the text of the message.
     */
    public void setContent(String text) {
        this.text = text;
    }

    /**
     * Returns the content of this message content object.
     * @return a string with its content
     */
    public String getContent() {
        return this.text;
    }

    public Image getImage() throws NoSuchFieldException {
        if (this.contentType.equalsIgnoreCase("IMAGE"))
            return this.image;
        else
            throw new NoSuchFieldException("Message has no image");
    }

    public void setContentType(String value) {
        this.contentType = value;
    }

    public String getContentType() {
        return this.contentType;
    }
    /**
     * Returns a JSONElement with the content of this message content object.
     * @return a JSONElement representing this object.
     */
    public JsonElement toJson(Format format) {
        return this.toFormat(format).toJSON();
    }

    /**
     * Sets the content of this message content object from its JSON representation.
     * @param json a JsonElement with the JSON representation of this message content object.
     */
    public void fromJson(JsonElement json) {
        Format[] formats = Format.getFormat(json);

        for (Format format : formats) {
            if (format instanceof MESSAGE_CONTENT) {
                this.fromFormat(format);
                break;
            }
        }

        throw new IllegalArgumentException("MESSAGE_CONTENT format expected in json parser.");
    }

    public void fromFormat(Format format) {
        if (format instanceof MESSAGE_CONTENT) {
            this.text = ((MESSAGE_CONTENT)format).CONTENT;
            this.contentType = ((MESSAGE_CONTENT)format).CONTENT_TYPE;
            if (this.contentType.equalsIgnoreCase("IMAGE"))
                this.image = new Image(this.text);
        } else {
            throw new IllegalArgumentException("MESSAGE_CONTENT format expected in format parser.");
        }
    }

    public Format toFormat(Format format) {
        if (format instanceof MESSAGE_CONTENT) {
            ((MESSAGE_CONTENT) format).CONTENT = this.text;
            ((MESSAGE_CONTENT) format).CONTENT_TYPE = this.contentType;
            return format;
        }


        throw new IllegalArgumentException("MESSAGE_CONTENT format expected in format parser.");
    }
}
