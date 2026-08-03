ALTER TABLE memories
ADD COLUMN normalized_value VARCHAR(500);

ALTER TABLE memories
ADD COLUMN observation_count INTEGER;

ALTER TABLE memories
ADD COLUMN status VARCHAR(20);

UPDATE memories
SET normalized_value = value
WHERE normalized_value IS NULL;

UPDATE memories
SET observation_count = 1
WHERE observation_count IS NULL;

UPDATE memories
SET status = 'CURRENT'
WHERE status IS NULL;

ALTER TABLE memories
ALTER COLUMN normalized_value SET NOT NULL;

ALTER TABLE memories
ALTER COLUMN observation_count SET NOT NULL;

ALTER TABLE memories
ALTER COLUMN status SET NOT NULL;