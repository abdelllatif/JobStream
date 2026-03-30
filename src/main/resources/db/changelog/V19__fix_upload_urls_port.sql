-- Migration: Fix upload URLs port from 8080 to 8081
-- Description: Updates all photo and CV URLs in the profiles table to use port 8081 instead of 8080

UPDATE profiles
SET photo_url = REPLACE(photo_url, 'localhost:8080', 'localhost:8081')
WHERE photo_url LIKE '%localhost:8080%';

UPDATE profiles
SET cv_url = REPLACE(cv_url, 'localhost:8080', 'localhost:8081')
WHERE cv_url LIKE '%localhost:8080%';

UPDATE companies
SET logo_url = REPLACE(logo_url, 'localhost:8080', 'localhost:8081')
WHERE logo_url LIKE '%localhost:8080%';

