-- =====================================================
-- NOTE FULL-TEXT SEARCH
-- =====================================================

ALTER TABLE notes
ADD COLUMN search_vector tsvector;

CREATE OR REPLACE FUNCTION update_note_search_vector()
RETURNS trigger
LANGUAGE plpgsql
AS
$$
BEGIN
    NEW.search_vector :=
        setweight(
            to_tsvector(
                'simple',
                COALESCE(NEW.title, '')
            ),
            'A'
        )
        ||
        setweight(
            to_tsvector(
                'simple',
                COALESCE(NEW.content, '')
            ),
            'B'
        );

    RETURN NEW;
END;
$$;

CREATE TRIGGER trg_notes_search_vector
BEFORE INSERT OR UPDATE
ON notes
FOR EACH ROW
EXECUTE FUNCTION update_note_search_vector();

UPDATE notes
SET search_vector =
    setweight(
        to_tsvector(
            'simple',
            COALESCE(title, '')
        ),
        'A'
    )
    ||
    setweight(
        to_tsvector(
            'simple',
            COALESCE(content, '')
        ),
        'B'
    );

CREATE INDEX idx_notes_search_vector
ON notes
USING GIN(search_vector);