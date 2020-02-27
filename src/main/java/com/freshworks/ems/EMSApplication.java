package com.freshworks.ems;

import com.freshworks.ems.health.EMSHealthCheck;
import com.freshworks.ems.resources.DepartmentResource;
import com.freshworks.ems.resources.EmployeeResource;
import com.freshworks.ems.service.DepartmentService;
import com.freshworks.ems.service.EmployeeService;
import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class EMSApplication extends Application<EMSConfiguration> {
    private DepartmentService departmentService;
    private EmployeeService employeeService;

    public static void main(String[] args) throws Exception {
        new EMSApplication().run(args);
    }

    @Override
    public void initialize(Bootstrap<EMSConfiguration> bootstrap) {
        super.initialize(bootstrap);
    }

    @Override
    public void run(EMSConfiguration configuration, Environment environment){
        final EMSHealthCheck healthCheck = new EMSHealthCheck("EMS Health Check");
        environment.healthChecks().register("HealthCheck", healthCheck);
        departmentService = new DepartmentService(new EMSConfiguration().getJsonParser());
        employeeService = new EmployeeService(departmentService);
        environment.jersey().register(new EmployeeResource(environment.getValidator(), employeeService));
        environment.jersey().register(new DepartmentResource(environment.getValidator(), departmentService, employeeService));
    }
}