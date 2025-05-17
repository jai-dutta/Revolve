package com.jaidutta.revolve.controller;

import com.jaidutta.revolve.controller.dto.ApiResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController @RequestMapping("/api/users") public class UserController {

    @GetMapping("/me") public ResponseEntity<?> getCurrentUser() {

        Authentication authentication = SecurityContextHolder.getContext()
                                                             .getAuthentication();

        if (authentication.getPrincipal() instanceof UserDetails userDetails) {
            String username = userDetails.getUsername();
            return ResponseEntity.ok(ApiResponseDto.success("Hello " + username));

        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                 .body(ApiResponseDto.error("server.error",
                                                            "Unexpected principal type found"));
        }
    }
}
