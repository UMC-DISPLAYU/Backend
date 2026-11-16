UPDATE Agreement
SET isRequired = 0
WHERE code = 'PRIVACY_COLLECTION_USE'
  AND version = '1.0'
  AND effectiveDate = '2026-08-01'
  AND isRequired = 1;
