package com.freshworks.ems;

import io.dropwizard.Configuration;
import org.json.simple.parser.JSONParser;
import java.io.FileReader;

public class EMSConfiguration extends Configuration {
    private Object object;

    public EMSConfiguration() {

    }

    public Object getJsonParser(){
        JSONParser jsonParser = new JSONParser();
        try (FileReader reader = new FileReader("/Users/akpathivada/Desktop/database/departments.json")){
            object = jsonParser.parse(reader);
        }
        catch (Exception e) {

        }
        return object;
    }
}





/*
 @JsonProperty
    public String getFilename() {
        return filename;
    }

    @JsonProperty
    public void setFilename(String template) {
        this.filename = filename;
    }
 */