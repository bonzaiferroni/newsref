Transaction attempt #0 failed: org.postgresql.util.PSQLException: ERROR: column page_content.composite_id does not exist
Position: 146. Statement(s): SELECT "content"."text" FROM page_content LEFT JOIN "content" ON "content".id = page_content.content_id WHERE page_content.page_id = ? ORDER BY page_content.composite_id ASC
org.jetbrains.exposed.exceptions.ExposedSQLException: org.postgresql.util.PSQLException: ERROR: column page_content.composite_id does not exist
* I got this when I tried sorting by an id on a CompositeIdTable