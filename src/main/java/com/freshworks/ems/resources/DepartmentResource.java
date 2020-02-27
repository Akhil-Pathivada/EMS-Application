// This is the Department REST Controller

package com.freshworks.ems.resources;

import com.freshworks.ems.exceptions.DepartmentNotFound;
import com.freshworks.ems.model.ApiStatus;
import com.freshworks.ems.model.Department;
import com.freshworks.ems.service.DepartmentService;
import com.freshworks.ems.service.EmployeeService;

import javax.validation.Valid;
import javax.validation.Validator;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.net.URI;

@Path("/departments")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class DepartmentResource {
    private Validator validator;
    private final DepartmentService departmentService;

    public DepartmentResource(DepartmentService departmentService){
        this.departmentService = departmentService;
    }

    public DepartmentResource(Validator validator, DepartmentService departmentService, EmployeeService employeeService){
        this.validator = validator;
        this.departmentService = departmentService;
        departmentService.createEmployeeServiceObject(employeeService);
    }

    @POST
    public Response createDepartment(@Valid Department department) {
        try {
            departmentService.createDepartment(department);
            return Response.created(new URI("/")).entity(department).build();
        }
        catch (Exception e){
            return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                            "Internal Server Error"))
                    .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    public Response getAllDepartments() {
        try {
            return Response.ok(departmentService.getAllDepartments()).build();
        }
        catch (Exception e){
            return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                         "Internal Server Error"))
                            .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    @Path("/{depId}")
     public Response getDepartmentById(@PathParam("depId") Integer depId){
        try {
            return Response.ok(departmentService.getDepartmentById(depId)).build();
        }
        catch (DepartmentNotFound e){
            StringBuilder message = new StringBuilder("Department ");
            return  Response.status(Response.Status.NOT_FOUND)
                            .entity(new ApiStatus(Response.Status.NOT_FOUND.getStatusCode(),
                                                 message.append(depId).append(" not found").toString()))
                            .type(MediaType.APPLICATION_JSON).build();
        }
        catch (Exception e){
            return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                         "Internal Server Error"))
                            .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @GET
    @Path("/{depId}/employees")
    public Response getAllEmployees(@PathParam("depId")Integer depId) throws Exception {
        try {
            return Response.ok(departmentService.getEmpsOfDepartment(depId)).build();
        }
        catch (DepartmentNotFound e){
            StringBuilder message = new StringBuilder("Department ");
            return  Response.status(Response.Status.NOT_FOUND)
                            .entity(new ApiStatus(Response.Status.NOT_FOUND.getStatusCode(),
                                    message.append(depId).append(" not found").toString()))
                            .type(MediaType.APPLICATION_JSON).build();
        }
        catch (Exception e){
            return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                         "Internal Server Error"))
                            .type(MediaType.APPLICATION_JSON).build();
        }
    }



    @DELETE
    @Path("/{depId}")
    public Response deleteDepartment(@PathParam("depId") Integer depId) {
        try{
             departmentService.removeDepartment(depId);
             return Response.noContent().build();
        }
        catch (DepartmentNotFound e){
            StringBuilder message = new StringBuilder("Department ");
            return  Response.status(Response.Status.NOT_FOUND)
                            .entity(new ApiStatus(Response.Status.NOT_FOUND.getStatusCode(),
                                                  message.append(depId).append(" not found").toString()))
                            .type(MediaType.APPLICATION_JSON).build();
        }
        catch (Exception e){
            return  Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                            .entity(new ApiStatus(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(),
                                                "Internal Server Error"))
                            .type(MediaType.APPLICATION_JSON).build();
        }
    }

    @PUT
    @Path("/{depId}/" )
    public Response updateDepartment(@PathParam("depId") Integer depId, @Valid Department department) {
        try {
            return Response.ok(departmentService.updateDepartment(depId, department)).entity(department).build();
        }
        catch (DepartmentNotFound e) {
            StringBuilder message = new StringBuilder("Department ");
            return  Response.status(Response.Status.NOT_FOUND)
                            .entity(new ApiStatus(Response.Status.NOT_FOUND.getStatusCode(),
                                                    message.append(depId).append(" not found").toString()))
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