package com.mjd507.malicious;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mjd507.malicious.MaliciousFileDetectorUtil.FileTypes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@Configuration
public class MaliciousFileDetectorConfig {

    @Bean
    public CsvMaliciousFileDetector csvMaliciousFileDetector() {
        return new CsvMaliciousFileDetector();
    }

    @Bean
    public JsonMaliciousFileDetector jsonMaliciousFileDetector() {
        return new JsonMaliciousFileDetector();
    }

    @Bean
    public ExcelMaliciousFileDetector excelMaliciousFileDetector() {
        return new ExcelMaliciousFileDetector();
    }

    static class CsvMaliciousFileDetector implements MaliciousFileDetector {

        @Override
        public boolean canUseThisDetector(MultipartFile multipartFile) {
            FileTypes fileType = MaliciousFileDetectorUtil.getFileType(multipartFile);
            return fileType == FileTypes.CSV;
        }

        @Override
        public void validateFileContent(MultipartFile multipartFile) throws IOException {


        }
    }

    static class JsonMaliciousFileDetector implements MaliciousFileDetector {
        @Override
        public boolean canUseThisDetector(MultipartFile multipartFile) {
            FileTypes fileType = MaliciousFileDetectorUtil.getFileType(multipartFile);
            return fileType == FileTypes.JSON;
        }

        @Override
        public void validateFileContent(MultipartFile multipartFile) throws IOException {
            try {
                new ObjectMapper().readTree(multipartFile.getInputStream());
            } catch (Exception e) {
                throw new RuntimeException("json file may contain malicious content ");
            }
        }
    }

    static class ExcelMaliciousFileDetector implements MaliciousFileDetector {
        @Override
        public boolean canUseThisDetector(MultipartFile multipartFile) {
            FileTypes fileType = MaliciousFileDetectorUtil.getFileType(multipartFile);
            return fileType == FileTypes.EXCEL;
        }

        @Override
        public void validateFileContent(MultipartFile multipartFile) throws IOException {

        }
    }
}
