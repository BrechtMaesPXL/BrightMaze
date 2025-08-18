package be.pxl.services.services;

import be.pxl.services.domain.Video;
import be.pxl.services.domain.dto.VideoRequest;
import be.pxl.services.enums.Status;
import be.pxl.services.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class VideoService implements IVideoService {

    private final VideoRepository videoRepository;

    @Value("${upload.dir}")
    protected String videoPath;

    @Override
    public ResponseEntity saveVideo(VideoRequest videoRequest, MultipartFile videoFile) {
        try {
            // Save video file to filesystem
            String savedFilePath = saveVideoToFileSystem(videoFile);

            // Create Video entity
            Video video = Video.builder()
                    .videoName(videoRequest.getVideoName())
                    .videoDescription(videoRequest.getVideoDescription())
                    .createdDate(new Date())
                    .status(Status.OWNED)
                    .videoPath(savedFilePath)
                    .build();


            // Return success response
            return ResponseEntity.ok(videoRepository.save(video));
        } catch (IOException e) {
            // Handle file saving exception
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
    private String saveVideoToFileSystem(MultipartFile videoFile) throws IOException {
        if (videoPath == null || videoPath.isBlank()) {
            throw new IllegalStateException("Video directory path is not configured");
        }

        // Ensure video file is not null/empty
        if (videoFile == null || videoFile.isEmpty()) {
            throw new IllegalArgumentException("Video file is missing or empty");
        }

        // Log videoPath
        System.out.println("Configured video directory: " + videoPath);

        // Resolve file name
        String sanitizedFileName = Paths.get(videoFile.getOriginalFilename()).getFileName().toString();
        Path videoFilePath = Paths.get(videoPath).resolve(sanitizedFileName);

        // Create directories
        Files.createDirectories(videoFilePath.getParent());

        // Handle duplicate files
        int randomLength = 5;
        while (Files.exists(videoFilePath)) {
            sanitizedFileName = RandomStringUtils.randomAlphanumeric(randomLength) + "_" + sanitizedFileName;
            videoFilePath = Paths.get(videoPath).resolve(sanitizedFileName);
        }

        // Log final file path
        System.out.println("Saving video file to: " + videoFilePath);

        // Save file
        try (InputStream inputStream = videoFile.getInputStream()) {
            Files.copy(inputStream, videoFilePath);
        }

        return videoFilePath.toString();
    }
}
