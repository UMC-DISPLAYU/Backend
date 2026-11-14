package com.example.demo.domain.user.infrastructure.persistence;

import com.example.demo.domain.user.application.port.UserWithdrawalCleanupPort;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class UserWithdrawalCleanupAdapter implements UserWithdrawalCleanupPort {

  private static final List<String> USER_ACTIVITY_DELETE_SQLS =
      List.of(
          "DELETE FROM DisplayLike WHERE userId = ?",
          "DELETE FROM DisplayArtworkLike WHERE userId = ?",
          "DELETE FROM PersonalArtworkLike WHERE userId = ?",
          "DELETE FROM ArtworkFeelingLike WHERE userId = ?",
          "DELETE FROM ArtworkFeelingReplyLike WHERE userId = ?",
          "DELETE FROM ArtworkQuestionLike WHERE userId = ?",
          "DELETE FROM ArtworkQuestionReplyLike WHERE userId = ?",
          "DELETE FROM PersonalArtworkFeelingLike WHERE userId = ?",
          "DELETE FROM PersonalArtworkFeelingReplyLike WHERE userId = ?",
          "DELETE FROM PersonalArtworkQuestionLike WHERE userId = ?",
          "DELETE FROM PersonalArtworkQuestionReplyLike WHERE userId = ?",
          "DELETE FROM DisplayReviewLike WHERE userId = ?",
          "DELETE FROM DisplayReviewReplyLike WHERE userId = ?",
          "DELETE FROM LoungePostLike WHERE userId = ?",
          "DELETE FROM LoungeCommentLike WHERE userId = ?",
          "DELETE FROM LoungePostScrap WHERE userId = ?");

  private final JdbcTemplate jdbcTemplate;

  public UserWithdrawalCleanupAdapter(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void cleanup(Long userId) {
    USER_ACTIVITY_DELETE_SQLS.forEach(sql -> jdbcTemplate.update(sql, userId));

    jdbcTemplate.update(
        """
        DELETE FROM Memo
        WHERE archiveDisplayId IN (SELECT archiveDisplayId FROM ArchiveDisplay WHERE userId = ?)
           OR archiveWorkId IN (SELECT archiveWorkId FROM ArchiveWork WHERE userId = ?)
           OR archivePersonalWorkId IN (
               SELECT archivePersonalWorkId FROM ArchivePersonalWork WHERE userId = ?)
        """,
        userId,
        userId,
        userId);
    jdbcTemplate.update("DELETE FROM ArchiveDisplay WHERE userId = ?", userId);
    jdbcTemplate.update("DELETE FROM ArchiveWork WHERE userId = ?", userId);
    jdbcTemplate.update("DELETE FROM ArchivePersonalWork WHERE userId = ?", userId);

    jdbcTemplate.update(
        "DELETE FROM ArchiveArtist WHERE userId = ? OR artistUserId = ?", userId, userId);
    jdbcTemplate.update(
        """
        DELETE FROM AreaOfActivity
        WHERE artistProfileId IN (SELECT artistProfileId FROM ArtistProfile WHERE userId = ?)
        """,
        userId);
    jdbcTemplate.update("DELETE FROM ArtistProfile WHERE userId = ?", userId);

    jdbcTemplate.update(
        "DELETE FROM DisplayInvitation WHERE userId = ? OR userId2 = ?", userId, userId);
    jdbcTemplate.update("DELETE FROM SchoolEmailVerification WHERE userId = ?", userId);
    jdbcTemplate.update("DELETE FROM UserAgreement WHERE userId = ?", userId);
  }
}
