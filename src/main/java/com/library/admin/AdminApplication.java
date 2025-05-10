package com.library.admin;

import org.glassfish.jersey.server.ResourceConfig;
import jakarta.ws.rs.ApplicationPath;

@ApplicationPath("/admin")
public class AdminApplication extends ResourceConfig {
    public AdminApplication() {
        // Only load admin-side resource classes
        packages("com.library.admin");
    }
}
