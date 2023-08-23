package vn.vnpay.mapper;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.dto.UserInfoDTO;
import vn.vnpay.model.UserInfo;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Slf4j
public class UserInfoMapper {
    private static UserInfoMapper instance;

    public static UserInfoMapper getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UserInfoMapper();
        }
        return instance;
    }

    private UserInfoMapper() {
    }

    public UserInfoDTO convertToDTO(UserInfo userInfo) {
        if (Objects.isNull(userInfo)) {
            log.info("[convertToDTO] User is null");
            return null;
        }
        log.info("[convertToDTO] Convert user[{}] to DTO", userInfo.getId());
        return UserInfoDTO.builder()
                .id(userInfo.getId())
                .firstName(userInfo.getFirstName())
                .lastName(userInfo.getLastName())
                .address(userInfo.getAddress())
                .image(userInfo.getImage())
                .age(userInfo.getAge())
                .phone(userInfo.getPhone())
                .userId(userInfo.getUserId())
                .build();
    }
}
