package be.pxl.services.services;

import be.pxl.services.domain.dto.VideoRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

public interface IVideoService {

    ResponseEntity saveVideo(VideoRequest videoRequest, MultipartFile videoFile);

}
