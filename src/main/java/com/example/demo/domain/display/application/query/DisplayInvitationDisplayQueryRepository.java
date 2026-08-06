package com.example.demo.domain.display.application.query;

import java.util.List;

public interface DisplayInvitationDisplayQueryRepository {

  List<DisplayInvitationDisplayQueryResult> findPendingInvitationDisplays(Long inviteeUserId);
}
