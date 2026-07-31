CREATE INDEX idx_notes_full_text_search
ON notes
USING GIN (
    to_tsvector(
        'english',
        coalesce(title, '') || ' ' || coalesce(content, '')
    )
);