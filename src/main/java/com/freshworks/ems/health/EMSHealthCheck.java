package com.freshworks.ems.health;

import com.codahale.metrics.health.HealthCheck;

public class EMSHealthCheck extends HealthCheck {
    private String name;

    public EMSHealthCheck(String name){
        this.name = name;
    }

    @Override
    protected Result check(){
        if(name.isEmpty()){
            return Result.unhealthy("EMS Application is not Healthy");
        }
        return Result.healthy("EMS Applcation is Healthy");
    }
}