package org.surro.springaidemo.controller;

import org.springframework.ai.audio.transcription.AudioTranscriptionOptions;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.audio.transcription.TranscriptionModel;
import org.springframework.ai.audio.tts.TextToSpeechModel;
import org.springframework.ai.audio.tts.TextToSpeechPrompt;
import org.springframework.ai.audio.tts.TextToSpeechResponse;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
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
    private TranscriptionModel transcriptionModel;
    private TextToSpeechModel speechModel;

    @Autowired
    public AudioToTextController(OpenAiAudioTranscriptionModel transcriptionModel, OpenAiAudioSpeechModel speechModel) {
        this.transcriptionModel = transcriptionModel;
        this.speechModel = speechModel;
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

    @PostMapping("/dubbing")
    public byte[] textToVoice(@RequestParam String text) {
        var speechOptions = OpenAiAudioSpeechOptions.builder()
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.5)
                .model(OpenAiAudioApi.TtsModel.GPT_4_O_MINI_TTS.value)
                .voice(OpenAiAudioApi.SpeechRequest.Voice.ALLOY)
                .build();
        TextToSpeechPrompt speechPrompt = new TextToSpeechPrompt(text, speechOptions);
        TextToSpeechResponse response = speechModel.call(speechPrompt);
        return response.getResult().getOutput();
    }
}
