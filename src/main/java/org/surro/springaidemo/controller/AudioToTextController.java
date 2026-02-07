package org.surro.springaidemo.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController()
@RequestMapping("/api/audio")
public class AudioToTextController {
    TranscriptionModel transcriptionModel;

    @Autowired
    public AudioToTextController(OpenAiAudioTranscriptionModel transcriptionModel) {
        this.transcriptionModel = transcriptionModel;
    }

    @PostMapping("/record")
    public String generateText(@RequestParam MultipartFile file) {
        AudioTranscriptionOptions transcriptionOptions = OpenAiAudioTranscriptionOptions.builder()
                .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.SRT)
                .temperature(0f)
                .language("uk")
                .build();
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(file.getResource(), transcriptionOptions);
        return transcriptionModel.call(prompt).getResult().getOutput();
    }
}
