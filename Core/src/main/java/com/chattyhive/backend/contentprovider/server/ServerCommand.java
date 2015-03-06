package com.chattyhive.backend.contentprovider.server;

import com.chattyhive.backend.contentprovider.AvailableCommands;
import com.chattyhive.backend.contentprovider.ExecutionLevel;
import com.chattyhive.backend.contentprovider.formats.Format;

import java.lang.reflect.Field;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.CookieStore;
import java.net.HttpCookie;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.TreeSet;

/*
 * Created by Jonathan on 11/07/2014.
 */
public class ServerCommand {
    /*************************************/
    /*      SERVER COMMAND CLASS         */
    /*************************************/

    private ServerCommandDefinition command;
    private IServerUser serverUser;

    private ExecutionLevel executionLevel;

    private ArrayList<Format> inputFormats;
    private ArrayList<Format> paramFormats;

    private ArrayList<Format> resultFormats;
    private int resultCode;

    public ServerCommand (IServerUser serverUser, AvailableCommands command, Format... formats) {
        this.serverUser = serverUser;
        this.command = ServerCommandDefinition.GetCommand(command);

        this.inputFormats = new ArrayList<Format>();
        this.paramFormats = new ArrayList<Format>();

        for (Format format : formats) {
            if (this.command.getInputFormats().contains(format.getClass()))
               this.inputFormats.add(format);

            if (this.command.getParamFormats().contains(format.getClass()))
                this.paramFormats.add(format);
        }

        if (!this.checkFormats())
            throw new IllegalArgumentException("Required formats not specified.");
    }

    public ServerCommandDefinition getCommand() {
        return this.command;
    }
    public String getUrl() { //TODO: Edit this to support optional parameters
        String url = this.command.getUrl();
        int paramIndex = url.indexOf('[');
        //TODO: Algoritmo: 1) Buscar un %_ o un %+. 2) Buscar el %% correspondiente a éste abrir. 3) Enviar lo del medio a recursiveParseURL 4) Sustituir y repetir hasta terminar la url
        while (paramIndex > -1) {
            int endParamIndex = url.indexOf(']');
            if (endParamIndex > -1) {
                String parameter = url.substring(paramIndex + 1, endParamIndex);
                int dotIndex = parameter.indexOf('.');
                if (dotIndex > -1) {
                    try {
                        String value = this.getUrlParameterValue(parameter, paramFormats);
                        url = url.replace(String.format("[%s]", parameter), value);
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
            paramIndex = url.indexOf('[',paramIndex);
        }
        return url;
    }

    private String recursiveParseURL(String fragment,boolean Optional) {
        String url = fragment;
        //TODO: Algoritmo. Si comienza con %+ es un parámetro. Si comienza con %_ debe contener
        // un %+ con el parámtro; además el parámetro será opcional. Entre %_ y %+ está el prefijo.
        // Entre los cierres, %% y %%, está el sufijo.
        if (!Optional) {
            //Estoy en el último nivel. Resuelvo y devuelvo un valor.
        } else {
            //Quizá no esté en el último nivel. Verifico y ya devuelvo según si hay valor o no.
        }
        return url;
    }

    private String getUrlParameterValue(String parameter) throws NoSuchFieldException, IllegalAccessException {
        Format parameterFormat = null;

        int dotIndex = parameter.indexOf('.');
        int preIndex = 0;

        String formatName = parameter.substring(preIndex,dotIndex);

        for (Format f : this.paramFormats)
            if (this.command.getParamFormats().contains(f.getClass()))
                if (f.getClass().getSimpleName().equalsIgnoreCase(formatName)) {
                    parameterFormat = f;
                    break;
                }

        if (parameterFormat == null)
            throw new NullPointerException(String.format("No format specified for parameter %s.",parameter));

        preIndex = dotIndex+1;
        dotIndex = parameter.indexOf('.',preIndex);
        String fieldName;
        while (dotIndex > -1) {
            fieldName = parameter.substring(preIndex,dotIndex);
            Field field = parameterFormat.getClass().getField(fieldName);
            if (field.getType().getSuperclass().equals(Format.class)) {
                parameterFormat = (Format)field.get(parameterFormat);
            } else {
                throw new ClassCastException("Parametrized URLs can only access sub-fields of Format type fields.");
            }
            preIndex = dotIndex+1;
            dotIndex = parameter.indexOf('.',preIndex);
        }

        fieldName = parameter.substring(preIndex);
        Field field = parameterFormat.getClass().getField(fieldName);

        return field.get(parameterFormat).toString();
    }
    public String getBodyData() {
        if ((this.inputFormats == null) || (this.inputFormats.isEmpty()) || (this.command.getInputFormats().isEmpty())) return null;

        String bodyData = "";

        for (Format format : this.inputFormats) {
            if (this.command.getInputFormats().contains(format.getClass())) {
                String jsonString = format.toJSON().toString();
                bodyData += ((bodyData.isEmpty())?"{":", ") + jsonString.substring(1,jsonString.length()-1);
            }
        }

        bodyData += "}";

        return (!bodyData.equalsIgnoreCase("}"))?bodyData:"";
    }

    public Boolean checkCookies() {
        if (this.command.getRequiredCookies() == null) return true;

        for (String cookie : this.command.getRequiredCookies())
            if (this.serverUser.getAuthToken(cookie) == null)
                return false;

        return true;
    }
    public ArrayList<String> getUnsatisfyingCookies() {
        ArrayList<String> result = new ArrayList<String>();

        if (this.command.getRequiredCookies() == null) return null;

        for (String cookie : this.command.getRequiredCookies())
            if (this.serverUser.getAuthToken(cookie) == null)
                result.add(cookie);

        return (result.size() > 0)?result:null;
    }

    private Boolean checkFormats() {
        TreeSet<Class<?>> inputFormatClasses = new TreeSet<Class<?>>();
        for (Format format : this.inputFormats)
            inputFormatClasses.add(format.getClass());

        for (Class<?> formatClass : this.command.getRequiredInputFormats())
            if (!inputFormatClasses.contains(formatClass)) return false;

        TreeSet<Class<?>> paramFormatClasses = new TreeSet<Class<?>>();
        for (Format format : this.paramFormats)
            paramFormatClasses.add(format.getClass());

        for (Class<?> formatClass : this.command.getRequiredParamFormats())
            if (!paramFormatClasses.contains(formatClass)) return false;

        return true;
    }
}

