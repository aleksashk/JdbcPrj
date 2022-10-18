package com.aleksandrphilimonov.converter;

import com.aleksandrphilimonov.dao.UserModel;
import com.aleksandrphilimonov.service.UserDTO;

public class UserModelToUserDtoConverter implements Converter<UserModel, UserDTO> {
    @Override
    public UserDTO convert(UserModel source) {
        UserDTO userDTO = new UserDTO();
        userDTO.setId(source.getId());
        userDTO.setEmail(source.getEmail());

        return userDTO;
    }
}
