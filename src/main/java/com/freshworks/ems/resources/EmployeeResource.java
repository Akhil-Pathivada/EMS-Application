// This is the Employee REST Controller

package com.freshworks.ems.resources;

import com.freshworks.ems.exceptions.DepartmentNotFound;
import com.freshworks.ems.exceptions.EmployeeNotFound;
import com.freshworks.ems.model.ApiStatus;
import com.freshworks.ems.model.Employee;
import com.freshworks.ems.service.EmployeeService;
import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/employees")
@Produces(MediaType.APPLICATION_JSON)
public class EmployeeResource {
    private Validator validator;
    private final EmployeeService employeeService ;

    public EmployeeResource(EmployeeService employeeService){
        this.employeeService = employeeService;
    }

    public EmployeeResource(Validator validator, EmployeeService employeeService){
        this.validator = validator;
        this.employeeService = employeeService;
    }
    
    @POST
    public Response createEmployee(@Valid Employee employee){
        try {
            employeeService.createEmployee(employee);
            return Response.created(new URI("/employees/")).entity(employee).build();
        }
        catch (DepartmentNotFound exp){
            StringBuilder message = new StringBuilder("Department ");
            return  Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ApiStatus(Response.Status.BAD_REQUEST.getStatusCode(),
                                    message.append(employee.getDepId()).append(" not exists").toString()))
                            .type(MediaType.APPLICATION_JSON).build();
        }
        catch (Exception ex){
            return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                         "Internal Server Error"))
                            .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllEmployees(@DefaultValue("") @QueryParam("value") String attribute) {
        try {
            return Response.ok(employeeService.getAllEmployees(attribute)).build();
        }
        catch (IllegalArgumentException e) {
            return  Response.status(Response.Status.NOT_FOUND)
                            .entity(new ApiStatus(Response.Status.NOT_FOUND.getStatusCode(),
                                    "Unidentified Attribute"))
                            .type(MediaType.APPLICATION_JSON)
                            .build();
        }
        catch (Exception e){
            return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                    "Internal Server Error"))
                            .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    @Path("/{empId}")
    public Response getEmployeeById(@PathParam("empId") Integer empId) {
        try {
            return Response.ok(employeeService.getEmployeeById(empId)).build();
        }
        catch (EmployeeNotFound e) {
            StringBuilder message = new StringBuilder("Employee ");
            return  Response.status(Response.Status.NOT_FOUND)
                            .entity(new ApiStatus(Response.Status.NOT_FOUND.getStatusCode(),
                                    message.append(empId).append(" not found").toString()))
                            .type(MediaType.APPLICATION_JSON).build();
        }
        catch (Exception e) {
             return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                         "Internal Server Error"))
                            .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @PUT
    @Path("/{empId}")
    public Response updateEmployee(@PathParam("empId") Integer empId, @Valid Employee employee) {
        try {
            return Response.ok(employeeService.updateEmployee(empId, employee)).build();
        }
        catch (EmployeeNotFound e) {
            StringBuilder message = new StringBuilder("Employee ");
            return  Response.status(Response.Status.NOT_FOUND)
                            .entity(new ApiStatus(Response.Status.NOT_FOUND.getStatusCode(),
                                    message.append(empId).append(" not found").toString()))
                            .type(MediaType.APPLICATION_JSON).build();
        }
        catch (DepartmentNotFound e){
            StringBuilder message = new StringBuilder("Department ");
            return  Response.status(Response.Status.BAD_REQUEST)
                            .entity(new ApiStatus(Response.Status.BAD_REQUEST.getStatusCode(),
                                    message.append(employee.getDepId()).append(" not exists").toString()))
                            .type(MediaType.APPLICATION_JSON).build();
        }
        catch (Exception e) {
            return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                    "Internal Server Error"))
                            .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @DELETE
    @Path("/{empId}")
    public Response deleteEmployee(@PathParam("empId") Integer empId){
        try{
            employeeService.deleteEmployee(empId);
            return  Response.noContent().build();
        }
        catch (EmployeeNotFound e){
            StringBuilder message = new StringBuilder("Employee ");
            return  Response.status(Response.Status.NOT_FOUND)
                            .entity(new ApiStatus(Response.Status.NOT_FOUND.getStatusCode(),
                                    message.append(empId).append(" not found").toString()))
                            .type(MediaType.APPLICATION_JSON).build();
        }
        catch (Exception e) {
            return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                    "Internal Server Error"))
                            .type(MediaType.APPLICATION_JSON).build();
        }
   }
}
