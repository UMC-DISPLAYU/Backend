package com.example.demo.domain.user.application.port;

public interface UserWithdrawalCleanupPort {

  void cleanup(Long userId);
}
