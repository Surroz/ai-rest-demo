package org.surro.springaidemo.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
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
        AudioTranscriptionPrompt prompt = new AudioTranscriptionPrompt(file.getResource());
        return transcriptionModel.call(prompt).getResult().getOutput();
    }
}
