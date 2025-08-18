package be.pxl.services.controller;

import be.pxl.services.domain.dto.ChatsessieRequest;
import be.pxl.services.domain.dto.ChatsessieResponse;
import be.pxl.services.services.IChatsessieService;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("api/chat")
public class ChatsessieController {
    private final IChatsessieService chatsessieService;

    public ChatsessieController(IChatsessieService chatsessieService) {
        this.chatsessieService = chatsessieService;
    }
    private static final Logger log = LoggerFactory.getLogger(ChatsessieController.class);



    @PostMapping("/create")
    public ResponseEntity<String> startNewSession(){
        log.info("Start new session");
        return new ResponseEntity<>(chatsessieService.startSession(), HttpStatus.CREATED);
    }
    @PostMapping
    public ResponseEntity<ChatsessieResponse> sendChat(@Valid @RequestBody ChatsessieRequest chatsessieRequest) {
        log.info("Send chat");

        ChatsessieResponse response = chatsessieService.sendMessage(chatsessieRequest);

        log.debug("Response: {}", response); // géén .toString()

        return new ResponseEntity<>(response, HttpStatus.OK);
    }





}
