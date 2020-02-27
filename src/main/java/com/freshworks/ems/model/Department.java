// Model of a Department Record

package com.freshworks.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Null;

public class Department {

    @Null(message = "Dep.Id should not be given")
    private Integer depId;

    @JsonProperty
    @NotEmpty(message = "Dep.name should not be empty")
    private String name;

    public Department(){

    }

    public Department(String name){
        this.name = name;
    }

    public Department(Integer depId, String name){
        this.depId = depId;
        this.name = name;
    }

    public void setId(Integer depId){
        this.depId = depId;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Integer getDepId(){
        return this.depId;
    }

    @Override
    public String toString() {
        return "{\"depId\":" + depId + ",\"name\":" + name +"}";
    }
}
