package com.jiandong.malicious;

import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

@UtilityClass
public class MaliciousFileDetectorUtil {

    public enum FileTypes {
        CSV,
        JSON,
        EXCEL
    }

    public FileTypes getFileType(MultipartFile multipartFile) {
        String originalFilename = Objects.requireNonNull(multipartFile.getOriginalFilename());
        String extension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toUpperCase();
        return FileTypes.valueOf(extension);
    }
}
