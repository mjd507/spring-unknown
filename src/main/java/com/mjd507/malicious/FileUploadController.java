package com.mjd507.malicious;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("file/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final MaliciousFileDetectorService maliciousFileDetectorService;

    @PostMapping
    public void upload(@RequestParam("file") MultipartFile multipartFile) throws IOException {
        maliciousFileDetectorService.detectFile(multipartFile);
        log.info("upload success");
    }

}
