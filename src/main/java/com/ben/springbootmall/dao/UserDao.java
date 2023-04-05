package com.ben.springbootmall.dao;

import com.ben.springbootmall.dto.UserRegisterRequest;
import com.ben.springbootmall.model.User;

public interface UserDao {

    User getUserbyId(Integer userId);
    Integer createUser(UserRegisterRequest userRegisterRequest);
}
