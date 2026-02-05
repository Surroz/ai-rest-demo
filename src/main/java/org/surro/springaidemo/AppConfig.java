package org.surro.springaidemo;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class AppConfig {
    @Bean
    public  VectorStore vectorStore(@Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel, JdbcTemplate jdbcTemplate ) {
        return PgVectorStore.builder(jdbcTemplate,embeddingModel).build();
//        return SimpleVectorStore.builder(embeddingModel).build();
    }


}
