package com.chattyhive.Core.BusinessObjects.Chats.Context;

import com.chattyhive.Core.BusinessObjects.Chats.Chat;
import com.chattyhive.Core.BusinessObjects.Hives.Hive;
import com.chattyhive.Core.BusinessObjects.Image;
import com.chattyhive.Core.BusinessObjects.Users.User;

/**
 * Created by J.Guzmán on 10/02/2015.
 */
public class ContextElement {
    public enum ElementType { Chat, Hive, PublicUser, PrivateUser, Unknown};

    private ElementType elementType;

    private Hive hive;
    private Chat chat;
    private User user;

    private Image image;
    private String name;
    private String description;

    public ElementType getElementType() {
        return elementType;
    }

    public Hive getHive() {
        return hive;
    }
    public Chat getChat() {
        return chat;
    }
    public User getUser() {
        return user;
    }

    public Image getImage() {
        return image;
    }
    public String getName() {
        return name;
    }
    public String getDescription() {
        return description;
    }


    public ContextElement(Image image, String name) {
        this (ElementType.Unknown,null,null,null,image,name);
    }
    public ContextElement(ElementType elementType,User user,Image image, String name) {
        this (elementType,null,null,user,image,name);
    }
    public ContextElement(Chat chat, Image image, String name) {
        this (ElementType.Chat,null,chat,null,image,name);
    }
    public ContextElement(Hive hive, Image image, String name) {
        this(ElementType.Hive,hive,null,null,image,name);
    }
    public ContextElement(ElementType elementType, Hive hive, Chat chat,User user, Image image, String name) {
        this(elementType, hive,chat,user,image,name,null);
    }

    public ContextElement(Image image, String name, String description) {
        this(ElementType.Unknown,null,null,null,image,name,description);
    }
    public ContextElement(ElementType elementType, User user, Image image, String name, String description) {
        this(elementType,null,null,user,image,name,description);
    }
    public ContextElement(Chat chat, Image image, String name, String description) {
        this(ElementType.Chat,null,chat,null,image,name,description);
    }
    public ContextElement(Hive hive, Image image, String name, String description) {
        this(ElementType.Hive,hive,null,null,image,name,description);
    }
    public ContextElement(ElementType elementType, Hive hive, Chat chat,User user, Image image, String name, String description) {
        this.elementType = elementType;
        this.hive = hive;
        this.chat = chat;
        this.user = user;
        this.image = image;
        this.name = name;
        this.description = description;
    }
}
