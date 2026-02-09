package org.surro.springaidemo;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.ListOutputConverter;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.surro.springaidemo.model.Movie;

import java.util.List;
import java.util.Map;

@RestController()
@RequestMapping("/api")
public class OpenAIController {

    private final OpenAiChatModel chatModel;
    private final ChatClient chatClient;
    private EmbeddingModel embeddingModel;
    @Autowired
    VectorStore vectorStore;


    @Autowired
    public OpenAIController(OpenAiChatModel chatModel,
                            @Qualifier("openAiEmbeddingModel") EmbeddingModel embeddingModel) {
        this.chatModel = chatModel;
        ChatMemory chatMemory = MessageWindowChatMemory.builder().build();
        chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .build();
        this.embeddingModel = embeddingModel;
    }


    @GetMapping("/model")
    public ResponseEntity<String> getModelAnswer(@RequestParam String question) {
        return ResponseEntity.ok(chatModel.call(question));
    }

    @GetMapping("/chat")
    public ResponseEntity<String> getClientAnswer(@RequestParam String question) {
        ChatResponse response = chatClient
                .prompt(question)
                .call()
                .chatResponse();

        System.out.println(response.getMetadata());
        String sResponse = response.getResult().getOutput().getText();
        return ResponseEntity.ok(sResponse);
    }

    @PostMapping("/recommend")
    public List<String> recommend(@RequestParam String genre, @RequestParam String year, @RequestParam String length) {
        String request = """
                Can you recommend movies of {genre}
                that come out in {year} year
                and no longer than {length} minutes
                IMDB rating must be above 7.4,
                give 3 variants
                """;
        PromptTemplate template = new PromptTemplate(request);
        return chatClient.prompt(template.create(Map.of("genre", genre, "year", year, "length", length)))
                .call().entity(new ListOutputConverter(new DefaultConversionService()));

    }

    @PostMapping("/movies")
    public List<Movie> recommendMovies(@RequestParam String genre, @RequestParam String year, @RequestParam String length) {
        List<Movie> movies = ChatClient.create(chatModel).prompt()
                .user("""
                Generate the filmography of 5 movies {genre}
                genre that come out in {year} year
                and no longer than {length} minutes
                """)
                .call()
                .entity(new ParameterizedTypeReference<List<Movie>>() {});
        return movies;
    }

    @PostMapping("/embedding")
    public float[] embedding(@RequestBody String input) {
        return embeddingModel.embed(input);
    }

    @PostMapping("/difference")
    public double difference (@RequestParam String firstInput, @RequestParam String secondInput) {
        float[] firstEmbedding = embeddingModel.embed(firstInput);
        float[] secondEmbedding = embeddingModel.embed(secondInput);
        double dotProduct = 0;
        double firstNorm = 0;
        double secondNorm = 0;

        for (int i = 0; i < firstEmbedding.length; i++) {
            dotProduct += firstEmbedding[i] * secondEmbedding[i];
            firstNorm += Math.pow(firstEmbedding[i], 2);
            secondNorm += Math.pow(secondEmbedding[i], 2);
        }

        return dotProduct * 100 / (Math.sqrt(firstNorm) * Math.sqrt(secondNorm));
    }

    @PostMapping("/product")
    public List<Document> getProduct(@RequestBody String input) {
        return vectorStore.similaritySearch(SearchRequest.builder().topK(2).query(input).build());
//        return vectorStore.similaritySearch(input);
    }

    @PostMapping("/rag/question")
    public ResponseEntity<String> getAnswerWithRag(@RequestBody String input) {
        String genContent = chatClient.prompt(input)
                .advisors(QuestionAnswerAdvisor.builder(vectorStore).build())
                .call()
                .content();
        return ResponseEntity.ok(genContent);
    }
}
