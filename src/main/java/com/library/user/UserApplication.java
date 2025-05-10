package com.library.user;

import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/users")
public class UserApplication extends ResourceConfig {
    public UserApplication() {
        // Only load user-side resource classes
        packages("com.library.user");
    }
}
