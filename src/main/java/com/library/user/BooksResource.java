package com.library.user;

import com.library.common.model.Book;
import com.library.common.model.BookRequest;
import com.library.common.operations.BookOperations;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.HeaderParam;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Path("books")
public class BooksResource {
    private final BookOperations bookOperations = new BookOperationsImpl();

    @GET
    @Path("/all")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getAllBooks() {
        List<Book> books = bookOperations.getAllBooks();
        if (books.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No books found").build();
        }
        return Response.ok(books).build();
    }

    @GET
    @Path("/{bookId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBookById(@PathParam("bookId") int bookId) {
        Book book = bookOperations.getBookById(bookId);
        if (book == null) {
            return Response.status(Response.Status.NOT_FOUND).entity("Book not found").build();
        }
        return Response.ok(book).build();
    }

    @GET
    @Path("/category")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getBooksByCategory(@QueryParam("category") @DefaultValue("") String category) {
        // Fetch books based on the category
        if(category.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST).entity("No category passed").build();
        }
        List<Book> books = bookOperations.getBooksByCategory(category);

        if (books.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("No books found for the category: " + category).build();
        }

        return Response.status(Response.Status.OK).entity(books).build();
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response searchBooks(@QueryParam("title") String title,
                                @QueryParam("author") String author,
                                @QueryParam("category") String category,
                                @QueryParam("publisher") String publisher) {

        List<Book> books = bookOperations.searchBooks(title, author, category, publisher);

        if (books == null || books.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("No books found matching the search criteria.")
                    .build();
        }

        return Response.status(Response.Status.OK).entity(books).build();
    }

    @POST
    @Path("/borrow")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response borrowBook(BookRequest bookRequest) {
        boolean isBookAvailable = bookOperations.borrowBook(bookRequest.getBookId());

        if (isBookAvailable) {
            return Response.status(Response.Status.OK).entity("Book borrowed successfully").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST).entity("Book is not available").build();
        }
    }

    @POST
    @Path("/return")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response returnBook(@HeaderParam("user_id") int userId, Book book) {
        // Check if the book is borrowed by the user and is available to return
        if (bookOperations.checkBookBorrowedByUser(book.getBookId(), userId)) {
            bookOperations.returnBook(book.getBookId());
            return Response.status(Response.Status.OK).entity("Book returned successfully").build();
        } else {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Book is not borrowed or user mismatch").build();
        }
    }
}
