package vn.vnpay.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.Objects;

public class ObjectMapperCommon {
    private static ObjectMapper instance;

    private ObjectMapperCommon() {
    }


    public static ObjectMapper getInstance() {
        if (Objects.isNull(instance)) {
            instance = new ObjectMapper();
        }

        return instance;
    }
}
