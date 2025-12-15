package com.job.mapper;

import com.job.dto.request.UserRequestDTO;
import com.job.dto.response.UserResponseDTO;
import com.job.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toEntity(UserRequestDTO dto);

    UserResponseDTO toResponse(User user);
}
