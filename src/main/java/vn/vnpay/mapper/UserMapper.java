package vn.vnpay.mapper;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.dto.UserDTO;
import vn.vnpay.model.User;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 22/08/2023
 **/
@Slf4j
public class UserMapper {
    private static UserMapper instance;

    public static UserMapper getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UserMapper();
        }
        return instance;
    }

    private UserMapper() {
    }

    public UserDTO convertToDTO(User user) {
        if (Objects.isNull(user)) {
            log.info("[convertToDTO] User is null");
            return null;
        }
        log.info("[convertToDTO] Convert user[{}] to DTO", user.getId());
        return UserDTO.builder()
                .id(user.getId())
                .useName(user.getUseName())
                .role(user.getRole())
                .build();
    }
}
