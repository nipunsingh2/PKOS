-- Remove obsolete unique constraint
ALTER TABLE memories
DROP CONSTRAINT IF EXISTS uk_memory_user_key;

-- Remove obsolete index
DROP INDEX IF EXISTS idx_memory_key;

-- Remove obsolete columns
ALTER TABLE memories
DROP COLUMN IF EXISTS display_name,
DROP COLUMN IF EXISTS memory_key,
DROP COLUMN IF EXISTS last_verified_at;