package com.jiandong.malicious;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MaliciousFileDetectorService {

    private final List<MaliciousFileDetector> maliciousFileDetectorList;

    public void detectFile(MultipartFile multipartFile) throws IOException {
        MaliciousFileDetector maliciousFileDetector = maliciousFileDetectorList.stream()
                .filter(detector -> detector.canUseThisDetector(multipartFile))
                .findFirst().get();

        maliciousFileDetector.validateDoubleExtension(multipartFile);
        maliciousFileDetector.validateFileContent(multipartFile);
    }
}
