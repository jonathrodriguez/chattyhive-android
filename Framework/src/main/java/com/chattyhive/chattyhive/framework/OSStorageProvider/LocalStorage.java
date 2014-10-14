package com.chattyhive.chattyhive.framework.OSStorageProvider;

import android.util.Log;

import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.formats.CHAT;
import com.chattyhive.backend.contentprovider.formats.COMMON;
import com.chattyhive.backend.contentprovider.formats.Format;
import com.chattyhive.backend.contentprovider.formats.HIVE;
import com.chattyhive.backend.contentprovider.formats.LOCAL_USER_PROFILE;
import com.chattyhive.backend.contentprovider.formats.LOGIN;
import com.chattyhive.backend.contentprovider.formats.MESSAGE;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_ACK;
import com.chattyhive.backend.contentprovider.formats.MESSAGE_LIST;
import com.chattyhive.backend.contentprovider.formats.PROFILE_ID;
import com.chattyhive.backend.contentprovider.formats.USER_PROFILE;
import com.chattyhive.backend.contentprovider.local.LocalStorageInterface;
import com.chattyhive.backend.util.events.CommandCallbackEventArgs;
import com.chattyhive.backend.util.events.EventHandler;
import com.chattyhive.backend.util.formatters.TimestampFormatter;
import com.google.gson.JsonParser;

import java.util.Arrays;
import java.util.Collection;

/**
 * Created by Jonathan on 03/10/2014.
 */
public class LocalStorage implements LocalStorageInterface {
    private LocalStorage() {}
    static LocalStorage instance;

    public static LocalStorage getLocalStorage() {
        if (instance == null) { instance = new LocalStorage(); }
        return instance;
    }

    @Override
    public Boolean PreRunCommand(AvailableCommands command, EventHandler<CommandCallbackEventArgs> Callback, Object CallbackAdditionalData, Format... formats) {
        Boolean result = false;
        LOCAL_USER_PROFILE local_user_profile = null;
        USER_PROFILE remote_user_profile = null;
        PROFILE_ID profile_id = null;
        switch (command) {
            case StartSession:  //Session
            case Login:         //Session
            case EmailCheck:    //Query
            case Explore:       //Query
            case Register:      //ImmediateResponsePush
            case Join:          //ImmediateResponsePush
                result = false;
                break;
            case SendMessage:   //ForcePush //TODO
                result = false;
                break;
            case GetMessages:   //Pull //TODO
                result = false;
                break;
            case LocalProfile:  //Pull
                result = false;
                String local_profile = UserLocalStorage.getUserLocalStorage().RecoverLocalUserProfile();
                if ((local_profile != null) && (!local_profile.isEmpty()))
                    local_user_profile = new LOCAL_USER_PROFILE(new JsonParser().parse(local_profile));
                result = (local_user_profile != null);
                if (result)
                    Callback.Run(this,new CommandCallbackEventArgs(command, Arrays.asList((Format)local_user_profile),null,CallbackAdditionalData));
                break;
            case ChatInfo:   //Pull //TODO
                result = false;
                break;
            case ChatList:      //Pull //TODO
                result = false;
                break;
            case UserProfile:   //Pull
                result = false;
                for(Format format : formats)
                    if (format instanceof PROFILE_ID)
                        profile_id = (PROFILE_ID) format;
                if ((profile_id != null) && (profile_id.USER_ID != null) && (!profile_id.USER_ID.isEmpty()) && (profile_id.PROFILE_TYPE != null) && (!profile_id.PROFILE_TYPE.isEmpty())) {
                    String remote_profile = UserLocalStorage.getUserLocalStorage().RecoverCompleteUserProfile(profile_id.USER_ID);
                    if ((remote_profile != null) && (!remote_profile.isEmpty()))
                        remote_user_profile = new USER_PROFILE(new JsonParser().parse(remote_profile));
                    if (remote_user_profile != null) {
                        if (profile_id.PROFILE_TYPE.equalsIgnoreCase("BASIC_PUBLIC")) {
                            remote_user_profile.USER_BASIC_PRIVATE_PROFILE = null;
                            remote_user_profile.USER_PRIVATE_PROFILE = null;
                            remote_user_profile.USER_PUBLIC_PROFILE = null;
                        } else if (profile_id.PROFILE_TYPE.equalsIgnoreCase("BASIC_PRIVATE")) {
                            remote_user_profile.USER_PRIVATE_PROFILE = null;
                            remote_user_profile.USER_BASIC_PUBLIC_PROFILE = null;
                            remote_user_profile.USER_PUBLIC_PROFILE = null;
                        } else if (profile_id.PROFILE_TYPE.equalsIgnoreCase("EXTENDED_PUBLIC")) {
                            remote_user_profile.USER_BASIC_PRIVATE_PROFILE = null;
                            remote_user_profile.USER_PRIVATE_PROFILE = null;
                        } else if (profile_id.PROFILE_TYPE.equalsIgnoreCase("EXTENDED_PRIVATE")) {
                            remote_user_profile.USER_BASIC_PUBLIC_PROFILE = null;
                            remote_user_profile.USER_PUBLIC_PROFILE = null;
                        } else if (profile_id.PROFILE_TYPE.equalsIgnoreCase("COMPLETE_PUBLIC")) {
                            remote_user_profile.USER_BASIC_PRIVATE_PROFILE = null;
                            remote_user_profile.USER_PRIVATE_PROFILE = null;
                        } else if (profile_id.PROFILE_TYPE.equalsIgnoreCase("COMPLETE_PRIVATE")) {
                            remote_user_profile.USER_PUBLIC_PROFILE = null;
                        }
                        result = ((remote_user_profile.USER_BASIC_PRIVATE_PROFILE != null) || (remote_user_profile.USER_PRIVATE_PROFILE != null) || (remote_user_profile.USER_BASIC_PUBLIC_PROFILE != null) || (remote_user_profile.USER_PUBLIC_PROFILE != null));
                        if (result)
                            Callback.Run(this, new CommandCallbackEventArgs(command, Arrays.asList((Format) remote_user_profile), null, CallbackAdditionalData));
                    }
                }
                break;
            case HiveInfo:      //Pull //TODO
                result = false;
                break;
        }
        return result;
    }

    @Override
    public Boolean PostRunCommand(AvailableCommands command, Format... formats) {
        Boolean result = false;
        COMMON common = null;
        HIVE hive = null;
        CHAT chat = null;
        LOCAL_USER_PROFILE local_user_profile = null;
        USER_PROFILE remote_user_profile = null;
        USER_PROFILE remote_user_profile_aux = null;
        PROFILE_ID profile_id = null;
        LOGIN login = null;
        MESSAGE message = null;
        MESSAGE_ACK message_ack = null;
        MESSAGE_LIST message_list = null;
        switch (command) {
            case StartSession:  //Session
            case EmailCheck:    //Query
            case Explore:       //Query
                result = false;
                break;
            case Login:         //Session
                for(Format format : formats)
                    if (format instanceof COMMON)
                        common = (COMMON) format;
                    else if (format instanceof LOGIN)
                        login = (LOGIN)format;

                if ((common == null) || (login == null) || (!common.STATUS.equalsIgnoreCase("OK")))
                    result = false;
                else {
                    LoginLocalStorage.getLoginLocalStorage().StoreLoginPassword(login.USER,login.PASS);
                    result = true;
                }
                break;
            case Register:      //ImmediateResponsePush
                for(Format format : formats)
                    if (format instanceof COMMON)
                        common = (COMMON) format;
                    else if (format instanceof LOCAL_USER_PROFILE)
                        local_user_profile = (LOCAL_USER_PROFILE) format;
                    else if (format instanceof LOGIN)
                        login = (LOGIN)format;

                if ((common == null) || (local_user_profile == null) || (login == null) || (!common.STATUS.equalsIgnoreCase("OK")))
                    result = false;
                else {
                    UserLocalStorage.getUserLocalStorage().StoreLocalUserProfile(local_user_profile.toJSON().toString());
                    LoginLocalStorage.getLoginLocalStorage().StoreLoginPassword(local_user_profile.USER_BASIC_PUBLIC_PROFILE.PUBLIC_NAME,login.PASS);
                    result = true;
                }
                break;
            case Join:          //ImmediateResponsePush
                for(Format format : formats)
                    if (format instanceof COMMON)
                        common = (COMMON) format;
                    else if (format instanceof HIVE)
                        hive = (HIVE) format;
                    else if (format instanceof CHAT)
                        chat = (CHAT)format;

                if ((common == null) || (hive == null) || (chat == null) || (!common.STATUS.equalsIgnoreCase("OK")))
                    result = false;
                else {
                    HiveLocalStorage.getHiveLocalStorage().StoreHive(hive.NAME_URL,hive.toJSON().toString());
                    GroupLocalStorage.getGroupLocalStorage().StoreGroup(chat.CHANNEL_UNICODE,chat.toJSON().toString());
                    result = true;
                }
                break;
            case SendMessage:   //ForcePush
                for(Format format : formats)
                    if (format instanceof COMMON)
                        common = (COMMON) format;
                    else if (format instanceof MESSAGE)
                        message = (MESSAGE) format;
                    else if (format instanceof MESSAGE_ACK)
                        message_ack = (MESSAGE_ACK)format;

                if ((common == null) || (message == null) || (message_ack == null) || (!common.STATUS.equalsIgnoreCase("OK")))
                    result = false;
                else {
                    message.ID = message_ack.ID;
                    message.SERVER_TIMESTAMP = message_ack.SERVER_TIMESTAMP;
                    MessageLocalStorage.getMessageLocalStorage().RemoveMessage(message.CHANNEL_UNICODE,String.format("%s@%s",message.USER_ID, TimestampFormatter.toString(message.TIMESTAMP)));
                    MessageLocalStorage.getMessageLocalStorage().StoreMessage(message.CHANNEL_UNICODE,message.ID,message.toJSON().toString());
                    result = true;
                }
                break;
            case GetMessages:   //Pull
                for(Format format : formats)
                    if (format instanceof COMMON)
                        common = (COMMON) format;
                    else if (format instanceof MESSAGE_LIST)
                        message_list = (MESSAGE_LIST) format;

                if ((common == null) || (message_list == null) || (message_list.MESSAGES == null) || (message_list.MESSAGES.size() == 0) || (!common.STATUS.equalsIgnoreCase("OK")))
                    result = false;
                else {
                    for (MESSAGE m : message_list.MESSAGES) {
                        MessageLocalStorage.getMessageLocalStorage().StoreMessage(m.CHANNEL_UNICODE, m.ID, m.toJSON().toString());
                    }
                    result = true;
                }
                break;
            case LocalProfile:  //Pull
                for(Format format : formats)
                    if (format instanceof COMMON)
                        common = (COMMON) format;
                    else if (format instanceof LOCAL_USER_PROFILE)
                        local_user_profile = (LOCAL_USER_PROFILE) format;

                if ((common == null) || (local_user_profile == null) || (!common.STATUS.equalsIgnoreCase("OK")))
                    result = false;
                else {
                    UserLocalStorage.getUserLocalStorage().StoreLocalUserProfile(local_user_profile.toJSON().toString());
                    result = true;
                }
                break;
            case ChatInfo:   //Pull //TODO
                result = false;
                break;
            case ChatList:      //Pull //TODO
                result = false;
                break;
            case UserProfile:   //Pull
                for(Format format : formats)
                    if (format instanceof COMMON)
                        common = (COMMON) format;
                    else if (format instanceof USER_PROFILE)
                        remote_user_profile = (USER_PROFILE) format;
                    else if (format instanceof PROFILE_ID)
                        profile_id = (PROFILE_ID) format;

                if ((common == null) || (remote_user_profile == null) || (profile_id == null) || (profile_id.USER_ID == null) || (profile_id.USER_ID.isEmpty()) || (!common.STATUS.equalsIgnoreCase("OK")))
                    result = false;
                else {
                    String remote_profile = UserLocalStorage.getUserLocalStorage().RecoverCompleteUserProfile(profile_id.USER_ID);
                    if ((remote_profile != null) && (!remote_profile.isEmpty()))
                        remote_user_profile_aux = new USER_PROFILE(new JsonParser().parse(remote_profile));
                    if (remote_user_profile_aux != null) {
                        if (remote_user_profile.USER_BASIC_PRIVATE_PROFILE != null)
                            remote_user_profile_aux.USER_BASIC_PRIVATE_PROFILE = remote_user_profile.USER_BASIC_PRIVATE_PROFILE;
                        if (remote_user_profile.USER_PRIVATE_PROFILE != null)
                            remote_user_profile_aux.USER_PRIVATE_PROFILE = remote_user_profile.USER_PRIVATE_PROFILE;
                        if (remote_user_profile.USER_BASIC_PUBLIC_PROFILE != null)
                            remote_user_profile_aux.USER_BASIC_PUBLIC_PROFILE = remote_user_profile.USER_BASIC_PUBLIC_PROFILE;
                        if (remote_user_profile.USER_PUBLIC_PROFILE != null)
                            remote_user_profile_aux.USER_PUBLIC_PROFILE = remote_user_profile.USER_PUBLIC_PROFILE;

                        UserLocalStorage.getUserLocalStorage().StoreCompleteUserProfile(profile_id.USER_ID,remote_user_profile_aux.toJSON().toString());
                    } else {
                        UserLocalStorage.getUserLocalStorage().StoreCompleteUserProfile(profile_id.USER_ID,remote_user_profile.toJSON().toString());
                    }
                    result = true;
                }
                break;
            case HiveInfo:      //Pull //TODO
                result = false;
                break;
        }
        return result;
    }

    @Override
    public Boolean FormatsReceived(Collection<Format> receivedFormats) {
        return null;
    }
}
