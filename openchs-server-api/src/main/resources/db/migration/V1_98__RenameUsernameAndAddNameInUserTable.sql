ALTER TABLE users
  RENAME COLUMN name TO username;
ALTER TABLE users
  ALTER COLUMN username SET NOT NULL;
ALTER TABLE users
  ADD COLUMN name CHARACTER VARYING(255);
