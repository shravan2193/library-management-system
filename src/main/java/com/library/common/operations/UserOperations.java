package com.library.common.operations;

import com.library.common.model.User;

public interface UserOperations {
    boolean registerUser(User user);

    boolean checkUser(User user);

    boolean updateUser(User user);
}
