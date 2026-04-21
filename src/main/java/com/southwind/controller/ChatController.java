package com.southwind.controller;

import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ChatController {

    @Autowired
    private OllamaChatModel ollamaChatModel;

    @GetMapping(value = "/chat",produces = "text/event-stream;charset=utf-8")
    public Flux<String> chat2(@RequestParam(value = "message",defaultValue = "你好") String message){
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.ollamaChatModel.stream(prompt.getContents());
    }

}
