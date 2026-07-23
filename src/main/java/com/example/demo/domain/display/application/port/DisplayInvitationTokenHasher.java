package com.example.demo.domain.display.application.port;

public interface DisplayInvitationTokenHasher {

  String hash(String rawToken);
}
