package vn.vnpay.service;

import lombok.extern.slf4j.Slf4j;
import vn.vnpay.core.bean.controller.response.Response;
import vn.vnpay.repository.UserInfoRepository;

import java.util.Objects;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Slf4j
public class UserInfoService {
    private final UserInfoRepository repository = UserInfoRepository.getInstance();

    private static UserInfoService instance;

    public static UserInfoService getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UserInfoService();
        }
        return instance;
    }

    private UserInfoService() {
    }

    public Response<Object> getUserInfo(Integer userId) {
//        try {
//            UserInfoDTO userInfoDTO = UserInfoMapper.convertToDTO(repository.findByUserId(userId));
//            if (Objects.isNull(userInfoDTO)) {
//                return Triple.of(NOT_FOUND.getCode(), NOT_FOUND.getMessage(), null);
//            }
//            return Triple.of(SUCCESS.getCode(), SUCCESS.getMessage(), userInfoDTO);
//        } catch (Exception e) {
//            log.error("[getUserInfo] Exception: ", e);
//            return Triple.of(FOUND_USER_INFO_ERROR.getCode(), FOUND_USER_INFO_ERROR.getMessage(), null);
//        }
        return null;
    }
}
