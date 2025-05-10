package com.library.user;

import com.library.common.model.BorrowRecord;
import com.library.common.model.User;
import com.library.common.operations.BookOperations;
import com.library.common.operations.UserOperations;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;


@Path("users")
public class UserResource {

    private final UserService userService = new UserService();
    private final UserOperations userOperations = new UserOperationsImpl();
    private final BookOperations bookOperations = new BookOperationsImpl();

    @PUT
    @Path("/register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(User userBody) {
        // Validate input
        if (userBody == null) {
            return Response.status(Response.Status.BAD_REQUEST).entity("Missing required fields").build();
        }

        // Construct full User object
        User user = new User();
        user.setUsername(userBody.getUsername());
        user.setPassword(userService.hashPassword(userBody.getPassword())); // Hash the password
        user.setEmail(userBody.getEmail());

        if(userOperations.checkUser(user)) {
            return Response.status(Response.Status.CONFLICT).entity("User already registered").build();
        }

        if (userOperations.registerUser(user)) {
            return Response.status(Response.Status.CREATED).entity("User registered successfully").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Registration failed").build();
        }
    }

    @PUT
    @Path("/{user_id}/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("user_id") int userId, User userUpdate) {
        userUpdate.setUserid(userId);

        String hashedPassword = userService.hashPassword(userUpdate.getPassword());
        userUpdate.setPassword(hashedPassword);

        boolean success = userOperations.updateUser(userUpdate);

        if (success) {
            return Response.ok("User updated successfully").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to update user").build();
        }
    }

    @GET
    @Path("/{user_id}/borrow-history")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBorrowHistory(@PathParam("user_id") int userId) {
        List<BorrowRecord> history = bookOperations.getBorrowHistory(userId);
        return Response.ok(history).build();
    }
}
