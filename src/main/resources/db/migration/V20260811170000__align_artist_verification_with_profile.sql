UPDATE `User` u
LEFT JOIN ArtistProfile ap ON ap.userId = u.userId
SET u.isVerified = 0
WHERE u.isVerified = 1
  AND ap.artistProfileId IS NULL;
