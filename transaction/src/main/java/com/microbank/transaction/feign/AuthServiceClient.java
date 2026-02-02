package com.microbank.transaction.feign;

import com.microbank.transaction.config.FeignConfig;
import com.microbank.transaction.dto.response.UserResponse;
import com.microbank.transaction.response.BaseApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(
        name = "auth-service",
        configuration = FeignConfig.class
)
public interface AuthServiceClient {

    @GetMapping("/api/v1/auth/users/me")
    BaseApiResponse<UserResponse> getCurrentUser();

}
