package org.surro.springaidemo;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.List;

@Configuration
public class DataInitializer {

    @Autowired
    VectorStore vectorStore;

    @PostConstruct
    public void initData() {
        TextReader textReader = new TextReader(new ClassPathResource("data_for_vector_db.txt"));

    //        TokenTextSplitter splitter = new TokenTextSplitter();
        TokenTextSplitter splitter = new TokenTextSplitter(100, 30, 20, 500, false);
        List<Document> documents
                = splitter.split(textReader.get());
        vectorStore.add(documents);

    }
}
