// Model of a Employee record
package com.freshworks.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;
import javax.validation.constraints.Null;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.DecimalMin;

public class Employee {
    @Null(message = "Employee Id should not be given")
    private Integer empId;

    @JsonProperty
    @NotNull(message = "Department Id should be given")
    private Integer depId;

    @JsonProperty
    @NotEmpty
    private String name;

    @JsonProperty
    @Pattern(regexp = ".+@.+\\.[a-z]+")
    private String email;

    @JsonProperty
    @DecimalMin(value = "1")
    private Integer age;

    public Employee(){

    }

    public Employee(String name){
        this.name = name;
    }

    public Employee(Integer depId, String name) {
        this.name = name;
        this.depId = depId;
    }

    public Employee(Integer depId, String name, Integer age) {
        this.name = name;
        this.depId = depId;
        this.age = age;
    }

    public Employee(Integer empId, Integer depId, String name){
        this.empId = empId;
        this.depId = depId;
        this.name = name;
    }

    public Employee(Integer depId, String name, String email){
        this.depId = depId;
        this.name = name;
        this.email = email;
    }

    public Employee(Integer empId, Integer depId, String name, Integer age){
        this.empId = empId;
        this.depId = depId;
        this.name = name;
        this.age = age;
    }

    public Integer getEmpId(){
        return this.empId;
    }

    public void setEmpId(Integer empId){
        this.empId = empId;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getName(){
        return this.name;
    }

    public Integer getAge(){
        return this.age;
    }

    public Integer getDepId() {
        return this.depId;
    }
}
