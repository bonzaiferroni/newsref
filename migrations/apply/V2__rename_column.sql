ALTER TABLE "source" ADD embedding vector(1536) NULL;
ALTER TABLE "source" DROP COLUMN embeddings;
