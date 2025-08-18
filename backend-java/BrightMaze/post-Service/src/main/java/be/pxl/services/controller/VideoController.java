package be.pxl.services.controller;


import be.pxl.services.domain.dto.VideoRequest;
import be.pxl.services.services.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/video")
@RequiredArgsConstructor
@Slf4j
public class VideoController {
    private final VideoService videoService;

    @PostMapping("/upload")
    public ResponseEntity uploadVideo(
            @RequestParam("file") MultipartFile videoFile,
            @ModelAttribute VideoRequest videoRequest) {
        return videoService.saveVideo(videoRequest, videoFile);
    }

    @GetMapping()
    public ResponseEntity test() {
        return ResponseEntity.ok("Test");
    }


}
