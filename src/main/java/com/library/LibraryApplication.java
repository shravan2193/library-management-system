package com.library;

import com.library.admin.AdminResource;
import com.library.user.BooksResource;
import com.library.user.UserResource;
import jakarta.ws.rs.ApplicationPath;
import org.glassfish.jersey.server.ResourceConfig;

@ApplicationPath("/")
public class LibraryApplication extends ResourceConfig {
    public LibraryApplication() {
        System.out.println(">>> Jersey App Initialized <<<");
        packages(LibraryApplication.class.getPackage().getName());
        register(UserResource.class);
        register(BooksResource.class);
        register(AdminResource.class);
    }
}
