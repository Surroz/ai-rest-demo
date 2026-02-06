package org.surro.springaidemo.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.image.ImageGeneration;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller("/api/image")
public class ImageGenerationController {
    private final ChatClient chatClient;
    private final ImageModel imageModel;

    @Autowired
    public ImageGenerationController(OpenAiChatModel chatModel, OpenAiImageModel imageModel) {
        this.chatClient = ChatClient.builder(chatModel).build();
        this.imageModel = imageModel;
    }

    @PostMapping("/generate")
    public ResponseEntity<String> generateImage(@RequestBody String prompt) {
        ImageGeneration imageGeneration = imageModel.call(new ImagePrompt(prompt,
                        OpenAiImageOptions.builder()
                                .quality("hd")
                                .height(1024)
                                .width(1792)
                                .build()
                )
        ).getResult();
        String url = imageGeneration.getOutput().getUrl();
        return ResponseEntity.ok(url);
    }

    @PostMapping("/describe")
    public ResponseEntity<String> describeImage(@RequestParam String prompt, @RequestParam MultipartFile file) {

        String content = chatClient.prompt()
                .user(message -> message
                        .text(prompt)
                        .media(MimeTypeUtils.parseMimeType(file.getContentType()), file.getResource())
                )
                .call().content();
        return ResponseEntity.ok(content);
    }


}
