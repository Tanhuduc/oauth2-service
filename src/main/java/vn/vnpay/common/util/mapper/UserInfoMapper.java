package vn.vnpay.common.util.mapper;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.bean.dto.UserInfoDTO;
import vn.vnpay.bean.entity.UserInfo;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Slf4j
public class UserInfoMapper {

    private UserInfoMapper() {
    }

    public static UserInfoDTO convertToDTO(UserInfo userInfo) {
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
