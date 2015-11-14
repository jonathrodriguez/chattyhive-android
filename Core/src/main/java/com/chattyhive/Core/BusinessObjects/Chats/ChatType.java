package com.chattyhive.Core.BusinessObjects.Chats;

/**
 * Created by Jonathan on 12/06/2014.
 */
public enum ChatType {
    PUBLIC,  //ChatType for Hive public chat.
    PUBLIC_COMMUNITY, //ChatType for Community public chat.
    PRIVATE_HIVEMATE, //ChatType for public/anonymous single chat.
    PRIVATE_GROUP_HIVEMATE, //ChatType for public/anonymous group chat.
    PRIVATE_FRIEND, //ChatType for private/mates single chat.
    PRIVATE_GROUP_FRIEND //ChatType for private/mates group chat.
}
