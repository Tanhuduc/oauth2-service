package vn.vnpay.usecase;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.tuple.Triple;
import vn.vnpay.config.pool.DBCPDataSource;
import vn.vnpay.dto.UserInfoDTO;
import vn.vnpay.mapper.UserInfoMapper;
import vn.vnpay.netty.request.GetUserInfoRequest;
import vn.vnpay.repository.UserInfoRepository;

import java.sql.Connection;
import java.util.Objects;

import static vn.vnpay.netty.Error.Error.FOUND_USER_INFO_ERROR;
import static vn.vnpay.netty.Error.Error.NOT_FOUND;
import static vn.vnpay.netty.Error.Error.SUCCESS;

/**
 * @Author: DucTN
 * Created: 23/08/2023
 **/
@Slf4j
public class UserInfoUseCase {
    private final UserInfoMapper mapper = UserInfoMapper.getInstance();
    private final UserInfoRepository repository = UserInfoRepository.getInstance();

    private static UserInfoUseCase instance;

    public static UserInfoUseCase getInstance() {
        if (Objects.isNull(instance)) {
            instance = new UserInfoUseCase();
        }
        return instance;
    }

    private UserInfoUseCase() {
    }

    public Triple<String, String, UserInfoDTO> getUserInfo(GetUserInfoRequest request) {
        Connection connection;
        try {
            connection = DBCPDataSource.getConnection();
            UserInfoDTO userInfoDTO = mapper.convertToDTO(repository.findByUserId(connection, request.getUserId()));
            connection.close();
            if (Objects.isNull(userInfoDTO)) {
                return Triple.of(NOT_FOUND.getCode(), NOT_FOUND.getMessage(), null);
            }
            return Triple.of(SUCCESS.getCode(), SUCCESS.getMessage(), userInfoDTO);
        } catch (Exception e) {
            log.error("[getUserInfo] Exception: ", e);
            return Triple.of(FOUND_USER_INFO_ERROR.getCode(), FOUND_USER_INFO_ERROR.getMessage(), null);
        }
    }
}
