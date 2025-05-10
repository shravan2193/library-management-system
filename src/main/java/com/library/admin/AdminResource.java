package com.library.admin;

import com.library.common.model.Book;
import com.library.common.model.BorrowRecord;
import com.library.common.model.Category;
import com.library.common.model.User;
import com.library.common.operations.AdminOperations;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;
import java.util.Map;

@Path("admin")
public class AdminResource {
    private final AdminOperations adminOperations = new AdminOperationsImpl();

    @POST
    @Path("/books/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addBook(Book book) {
        if (adminOperations.addBook(book)) {
            return Response.status(Response.Status.CREATED).entity("Book added successfully").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to add book").build();
        }
    }

    @PUT
    @Path("/{book_id}/update")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateBook(@PathParam("book_id") int bookId, Book book) {
        boolean updated = adminOperations.updateBook(bookId, book);
        if (updated) {
            return Response.ok("Book updated successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Book not found or update failed")
                    .build();
        }
    }

    @DELETE
    @Path("/books/{book_id}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteBook(@PathParam("book_id") int bookId) {
        try {
            boolean deleted = adminOperations.deleteBook(bookId);
            if (deleted) {
                return Response.ok("Book deleted successfully").build();
            } else {
                return Response.status(Response.Status.NOT_FOUND).entity("Book not found").build();
            }
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error deleting book").build();
        }
    }

    @POST
    @Path("/category/add")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response addCategory(Category category) {
        boolean success = adminOperations.addCategory(category.getName());

        if (success) {
            return Response.status(Response.Status.OK).entity("Category added successfully").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Category already exists or failed to add").build();
        }
    }

    @POST
    @Path("/books/{book_id}/categories")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response assignCategoriesToBook(@PathParam("book_id") int bookId, Map<String, List<Integer>> categoryInput) {
        List<Integer> categoryIds = categoryInput.get("category_ids");
        if (categoryIds == null || categoryIds.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No category IDs provided").build();
        }

        boolean success = adminOperations.assignCategoriesToBook(bookId, categoryIds);
        if (success) {
            return Response.status(Response.Status.OK).entity("Categories assigned successfully").build();
        } else {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to assign categories").build();
        }
    }

    @GET
    @Path("/users")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllUsers() {
        List<User> users = adminOperations.getAllUsers();

        if (users.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No users found").build();
        }

        return Response.status(Response.Status.OK).entity(users).build();
    }

    @PUT
    @Path("/users/{user_id}/promote")
    @Produces(MediaType.APPLICATION_JSON)
    public Response promoteUserToAdmin(@PathParam("user_id") int userId) {
        boolean updated = adminOperations.promoteUserToAdmin(userId);

        if (updated) {
            return Response.status(Response.Status.OK).entity("User promoted to ADMIN successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found or already ADMIN").build();
        }
    }

    @DELETE
    @Path("/users/{user_id}/delete")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("user_id") int userId) {
        boolean deleted = adminOperations.deleteUser(userId);

        if (deleted) {
            return Response.status(Response.Status.OK).entity("User deleted successfully").build();
        } else {
            return Response.status(Response.Status.NOT_FOUND).entity("User not found or could not be deleted").build();
        }
    }

    @GET
    @Path("/borrow-records")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBorrowRecords() {
        List<BorrowRecord> records = adminOperations.getAllBorrowRecords();

        if (records.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No borrow records found").build();
        }

        return Response.status(Response.Status.OK).entity(records).build();
    }
}
