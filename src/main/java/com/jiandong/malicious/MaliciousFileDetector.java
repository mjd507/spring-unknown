package com.jiandong.malicious;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface MaliciousFileDetector {

    default void validateDoubleExtension(MultipartFile multipartFile) {
        String originalFilename = multipartFile.getOriginalFilename();
        assert originalFilename != null;
        if (originalFilename.indexOf(".") != originalFilename.lastIndexOf(".")) {
            throw new RuntimeException("disallow filename with double extension");
        }
    }

    boolean canUseThisDetector(MultipartFile multipartFile);

    void validateFileContent(MultipartFile multipartFile) throws IOException;
}
