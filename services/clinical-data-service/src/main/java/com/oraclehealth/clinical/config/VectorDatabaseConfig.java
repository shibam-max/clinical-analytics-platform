package com.oraclehealth.clinical.config;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.transformers.TransformersEmbeddingModel;
import org.springframework.ai.vectorstore.PgVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

@Configuration
public class VectorDatabaseConfig {

    @Value("${spring.ai.openai.api-key:demo-key}")
    private String openAiApiKey;

    @Value("${app.vectordb.dimensions:384}")
    private int vectorDimensions;

    @Bean
    @Primary
    public EmbeddingModel embeddingModel() {
        // Use local transformer model for production (no API key required)
        return new TransformersEmbeddingModel("sentence-transformers/all-MiniLM-L6-v2");
    }

    @Bean
    public EmbeddingModel openAiEmbeddingModel() {
        // Fallback to OpenAI if API key is available
        if (!"demo-key".equals(openAiApiKey)) {
            return new OpenAiEmbeddingModel(new OpenAiApi(openAiApiKey));
        }
        return embeddingModel();
    }

    @Bean
    public VectorStore vectorStore(DataSource dataSource, EmbeddingModel embeddingModel) {
        return new PgVectorStore.Builder(new JdbcTemplate(dataSource), embeddingModel)
                .withSchemaName("clinical_vectors")
                .withTableName("clinical_embeddings")
                .withDimensions(vectorDimensions)
                .withDistanceType(PgVectorStore.PgDistanceType.COSINE_DISTANCE)
                .withRemoveExistingVectorStoreTable(false)
                .withInitializeSchema(true)
                .build();
    }
}