package com.example.demo.domain.displayartwork.presentation.request;

import com.example.demo.domain.displayartwork.application.command.AuthorSetupCommand;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record AuthorSetupRequest(
    @NotBlank String artistName,
    Long artistUserId,
    @Valid @NotNull CoAuthorsRequest coAuthors,
    @NotNull Long qaHandlerUserId) {

  public AuthorSetupCommand toCommand(Long artworkId) {
    return new AuthorSetupCommand(
        artworkId,
        artistName,
        artistUserId,
        coAuthors.userIds(),
        coAuthors.rawNames(),
        qaHandlerUserId);
  }

  public record CoAuthorsRequest(List<@NotNull Long> userIds, List<@NotBlank String> rawNames) {

    public List<Long> userIds() {
      return userIds == null ? List.of() : userIds;
    }

    public List<String> rawNames() {
      return rawNames == null ? List.of() : rawNames;
    }
  }
}
