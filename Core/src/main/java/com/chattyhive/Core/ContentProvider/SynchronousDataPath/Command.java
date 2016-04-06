package com.chattyhive.Core.ContentProvider.SynchronousDataPath;

import com.chattyhive.Core.ContentProvider.Formats.Format;
import com.chattyhive.Core.ContentProvider.Server.IServerUser;
import com.chattyhive.Core.ContentProvider.Server.ServerResponse;
import com.chattyhive.Core.Util.CallbackDelegate;
import com.google.gson.Gson;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/*
 * Created by Jonathan on 11/07/2014.
 */
public class Command {
    /*************************************/
    /*          COMMAND CLASS            */
    /*************************************/

    private final ArrayList<CallbackDelegate<Command>> callbackDelegates;

    private CommandDefinition commandDefinition;
    private IServerUser serverUser;

    private ServerResponse serverResponse;

    public void setServerResponse(ServerResponse serverResponse) {
        this.serverResponse = serverResponse;
    }
    public ServerResponse getServerResponse() {
        return this.serverResponse;
    }

    public void addCallbackDelegate(CallbackDelegate<Command> callbackDelegate) {
        synchronized (callbackDelegates) {
            this.callbackDelegates.add(callbackDelegate);
            this.callbackDelegates.notify();
        }
    }
    public int countCallbackDelegates() {
        synchronized (callbackDelegates) {
            return this.callbackDelegates.size();
        }
    }
    public CallbackDelegate<Command> popCallbackDelegate(long timeout) {
        CallbackDelegate<Command> callback = null;
        synchronized (callbackDelegates) {
            if (this.callbackDelegates.isEmpty()) {
                try {
                    this.callbackDelegates.wait(timeout);
                } catch (InterruptedException ignored) {
                }
            }

            if (!this.callbackDelegates.isEmpty()) {
                callback = callbackDelegates.remove(0);
            }
        }
        return callback;
    }

    public ExecutionLevel getExecutionLevel() {
        return executionLevel;
    }
    public void setExecutionLevel(ExecutionLevel executionLevel) {
        this.executionLevel = executionLevel;
    }

    public List<Object> getResultFormats() {
        return Collections.unmodifiableList(resultFormats);
    }
    public void setResultFormats(ArrayList<Object> resultFormats) {
        this.resultFormats = new ArrayList<>();
        for(Object format : resultFormats) {
            this.resultFormats.add(format);
        }
    }

    public int getResultCode() {
        return resultCode;
    }
    public void setResultCode(int resultCode) {
        this.resultCode = resultCode;
    }

    private ExecutionLevel executionLevel;

    private ArrayList<Object> inputFormats;
    private ArrayList<Object> paramFormats;

    private ArrayList<Object> resultFormats;
    private int resultCode;

    public Command(AvailableCommands commandDefinition, Format... formats) {
        this.serverUser = null;
        this.commandDefinition = CommandDefinition.GetCommand(commandDefinition);

        this.callbackDelegates = new ArrayList<>();

        this.inputFormats = new ArrayList<>();
        this.paramFormats = new ArrayList<>();

        for (Format format : formats) {
            if (this.commandDefinition.getInputFormats().contains(format.getClass()))
               this.inputFormats.add(format);

            if (this.commandDefinition.getParamFormats().contains(format.getClass()))
                this.paramFormats.add(format);
        }

        if (!this.checkFormats())
            throw new IllegalArgumentException("Required formats not specified.");
    }

    public CommandDefinition getCommandDefinition() {
        return this.commandDefinition;
    }
    public IServerUser getServerUser() {
        return this.serverUser;
    }
    public void setServerUser(IServerUser serverUser) {
        this.serverUser = serverUser;
    }
    public String getUrl() {
        String url = this.commandDefinition.getUrl();

        if (url.contains("(?P<") || url.contains("(?O<"))
            url = (new ParseURL(url)).getParsedURL();

        return url;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Command command = (Command) o;

        if (!commandDefinition.equals(command.commandDefinition))
            return false;
        if (inputFormats != null ? !inputFormats.equals(command.inputFormats) : command.inputFormats != null)
            return false;
        if (paramFormats != null ? !paramFormats.equals(command.paramFormats) : command.paramFormats != null)
            return false;
        if (serverUser != null ? !serverUser.equals(command.serverUser) : command.serverUser != null)
            return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = commandDefinition.hashCode();
        result = 31 * result + (serverUser != null ? serverUser.hashCode() : 0);
        result = 31 * result + (inputFormats != null ? inputFormats.hashCode() : 0);
        result = 31 * result + (paramFormats != null ? paramFormats.hashCode() : 0);
        return result;
    }

    private class ParseURL {
        private String initialValue;
        private ArrayList<ReplacementGroup> replacementGroups;
        private String parsedValue;

        private ParseURL(String initialValue) {
            this.initialValue = initialValue;
            replacementGroups = new ArrayList<ReplacementGroup>();
            this.parse();
        }

        private void parse() {
            //Ejemplo: users/(?P<>[USER.USER_ID]) <- Parámetro obligatorio USER.USER_ID sin nombre. Esto se resolvería a: users/jonathan
            //Ejemplo: users/(?O<>[USER.USER_ID]/) <- Parámetro opcional USER.USER_ID sin nombre. Esto se resolvería a: users/jonathan/ ó si no existe a: users/
            //                                        Sólo es posible definir este tipo de parámetros como último elemento de la consulta, pero pueden encadenarse varios
            //Ejemplo: users/(?P<username>[USER.USER_ID]) <- Parámetro obligatorio USER.USER_ID con nombre. Esto se resolvería a: users/?username=jonathan
            //Ejemplo: users/(?O<username>[USER.USER_ID]) <- Parámetro opcional USER.USER_ID con nombre. Esto se resolvería a: users/?username=jonathan ó si no existe a: users/
            //Ejemplo: explore/(?O<query>[EXPLORE.QUERY])(?P<category>[EXPLORE.CATEGORY]) <- Parámtros opcional y obligatorio con nombre. Esto se resolvería a: explore/?query=universo&category=10.01 ó a: explore/?category=10.01
            //Ejemplo: explore/(?O<query>[EXPLORE.QUERY])(?O<category>[EXPLORE.CATEGORY]) <- Parámtros opcionales con nombre. Esto se resolvería a: explore/?query=universo&category=10.01 ó a: explore/?category=10.01 ó a: explore/?query=universo ó a: explore/
            //                                           Los parámetros con nombre sólo pueden figurar al final de la URL. Siempre se generarán como "query parameters". Es posible encadenarlos de forma que la existencia de algunos dependa de la existencia del que los contiene.

            TreeSet<Integer> paramIndexOpenings = new TreeSet<Integer>();
            TreeSet<Integer> optionIndexOpenings = new TreeSet<Integer>();

            Integer paramIndex = this.initialValue.indexOf("(?P");
            while (paramIndex > -1) {
                paramIndexOpenings.add(paramIndex);
                paramIndex = this.initialValue.indexOf("(?P",paramIndex+1);
            }

            Integer optionIndex = this.initialValue.indexOf("(?O");
            while (optionIndex > -1) {
                optionIndexOpenings.add(optionIndex);
                optionIndex = this.initialValue.indexOf("(?O",optionIndex+1);
            }

            int nextCloseIndex = this.initialValue.indexOf(")");

            while (nextCloseIndex > -1) {
                if ((paramIndexOpenings.isEmpty()) && (optionIndexOpenings.isEmpty()))
                    throw new RuntimeException("Unexpected usage of parenthesis in url.");

                ReplacementGroup rg = new ReplacementGroup();
                paramIndex = paramIndexOpenings.floor(nextCloseIndex);
                optionIndex = optionIndexOpenings.floor(nextCloseIndex);

                if ((paramIndex != null) && ((optionIndex == null) || (paramIndex > optionIndex))) {
                    rg.startPosition = paramIndex;
                    rg.optional = false;
                    paramIndexOpenings.remove(paramIndex);
                }else if ((optionIndex != null) && ((paramIndex == null) || (optionIndex > paramIndex))) {
                    rg.startPosition = optionIndex;
                    rg.optional = true;
                    optionIndexOpenings.remove(optionIndex);
                } else
                    throw new RuntimeException("Bad syntax in url.");

                rg.endPosition = nextCloseIndex;
                int indexOfOpenName = this.initialValue.indexOf("<",rg.startPosition);
                int indexOfCloseName = this.initialValue.indexOf(">",rg.startPosition);
                int indexOfOpenValue = this.initialValue.indexOf("[",rg.startPosition);
                int indexOfCloseValue = this.initialValue.indexOf("]",rg.startPosition);

                if (indexOfCloseName == (indexOfOpenName+1)) {
                    rg.parameterName = "";
                    rg.queryParameter = false;
                } else {
                    rg.queryParameter = true;
                    rg.parameterName = this.initialValue.substring(indexOfOpenName+1,indexOfCloseName);
                }

                if (indexOfCloseValue == (indexOfOpenValue+1)) {
                    throw new RuntimeException("URL parameter declaration without value association.");
                } else {
                    rg.value = this.initialValue.substring(indexOfOpenValue+1,indexOfCloseValue);
                }

                if (nextCloseIndex > (indexOfCloseValue+1)) {
                    rg.suffix = new ParseURL(this.initialValue.substring(indexOfCloseValue+1,nextCloseIndex));
                }

                this.replacementGroups.add(rg);

                nextCloseIndex = this.initialValue.indexOf(")",nextCloseIndex+1);
            }

            if ((!paramIndexOpenings.isEmpty()) || (!optionIndexOpenings.isEmpty()))
                throw new RuntimeException("Bad syntax in url.");

            String finalURL = this.initialValue;
            Boolean queryParametersStarted = false;
            for (ReplacementGroup rg : replacementGroups) {
                String begin = finalURL.substring(0,rg.startPosition);
                String end = ((rg.endPosition+1)<finalURL.length())?finalURL.substring(rg.endPosition+1,finalURL.length()):"";

                String middle = "";
                String value = "";
                try { value = getUrlParameterValue(rg.value); } catch (Exception e) { value = ""; }

                if ((!rg.optional) && (value.isEmpty()))
                    throw new RuntimeException("Non optional parameter without value.");

                if ((rg.queryParameter) && ((rg.parameterName == null) || (rg.parameterName.isEmpty())))
                    throw new RuntimeException("Query parameter without name.");

                if (!rg.optional || !value.isEmpty()) {
                    if (rg.queryParameter) {
                        middle = ((queryParametersStarted)?"&":"?") + rg.parameterName + "=" + value;
                        queryParametersStarted = true;
                    } else {
                        middle = value;
                        queryParametersStarted = false;
                    }
                }

                if (rg.suffix != null)
                    middle += rg.suffix.getParsedURL();

                finalURL = begin + middle + end;
            }

            this.parsedValue = finalURL;
        }

        public String getParsedURL() {
            return parsedValue;
        }

        public String getInitialURL() {
            return initialValue;
        }
    }
    private class ReplacementGroup {
        int startPosition;
        int endPosition;

        Boolean optional;
        Boolean queryParameter;

        String parameterName;
        String value;

        ParseURL suffix;
    }

    private String getUrlParameterValue(String parameter) throws NoSuchFieldException, IllegalAccessException {
        Object parameterFormat = null;

        int dotIndex = parameter.indexOf('.');
        int preIndex = 0;

        String formatName = parameter.substring(preIndex,dotIndex);

        for (Object f : this.paramFormats)
            if (this.commandDefinition.getParamFormats().contains(f.getClass()))
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
                parameterFormat = field.get(parameterFormat);
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
        if ((this.inputFormats == null) || (this.inputFormats.isEmpty()) || (this.commandDefinition.getInputFormats().isEmpty())) return null;

        String bodyData = "";

        Gson gson = new Gson();

        for (Object format : this.inputFormats) {
            if (this.commandDefinition.getInputFormats().contains(format.getClass())) {
                String jsonString = gson.toJson(format);
                bodyData += ((bodyData.isEmpty())?((this.inputFormats.size() > 1)?"{":""):", ") + jsonString;
            }
        }

        if (this.inputFormats.size() > 1) {
            bodyData += "}";
        }

        return (!bodyData.equalsIgnoreCase("}"))?bodyData:"";
    }

    //FIXME
    private Boolean checkFormats() {
        TreeSet<Class> inputFormatClasses = new TreeSet<>();
        for (Object format : this.inputFormats)
            inputFormatClasses.add(format.getClass());

        for (Class formatClass : this.commandDefinition.getInputFormats())
            if (!inputFormatClasses.contains(formatClass)) return false;

        TreeSet<Class> paramFormatClasses = new TreeSet<>();
        for (Object format : this.paramFormats)
            paramFormatClasses.add(format.getClass());

        for (Class formatClass : this.commandDefinition.getParamFormats())
            if (!paramFormatClasses.contains(formatClass)) return false;

        return true;
    }


}

