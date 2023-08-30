package vn.vnpay.common.util.mapper;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.dto.UserDTO;
import vn.vnpay.bean.entity.User;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 22/08/2023
 **/
@Slf4j
public class UserMapper {

    private UserMapper() {
    }

    public static UserDTO convertToDTO(User user) {
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
