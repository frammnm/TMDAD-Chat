package com.example.demochat.controller;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.Random;

import com.example.demochat.model.Message;
import com.example.demochat.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/files")
public class FilesController {

    @Autowired
    private MessageService messageService;

    @Value("${spring.chat.filepath}")
    private String filesBasePath;

    @PostMapping("/upload")
    public ResponseEntity uploadToLocalFileSystem(@RequestPart("file") MultipartFile file, @RequestPart("message") Message m) {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        Random rand = new Random();
        rand.setSeed(Instant.now().getEpochSecond());
        String parsedFileName = Math.abs(rand.nextInt()) + "_" + fileName;
        Path path = Paths.get(filesBasePath + parsedFileName);
        try {
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/files/download/")
                .path(parsedFileName)
                .toUriString();

        m.setBody(m.getBody() + "<br> <a href='" + fileDownloadUri + "'>" + fileName + "</a>");

        if (m.getType().equals("Group")) {
            messageService.sendGroupMessage(m);
        } else if (m.getType().equals("Direct")) {
            messageService.sendMessage(m);
        } else {
            messageService.broadcastMessage(m);
        }
        return ResponseEntity.ok(fileDownloadUri);
    }

    @GetMapping("/download/{fileName:.+}")
    public ResponseEntity downloadFileFromLocal(@PathVariable String fileName) {
        Path path = Paths.get(filesBasePath + fileName);
        UrlResource resource = null;
        try {
            resource = new UrlResource(path.toUri());
        } catch ( MalformedURLException e) {
            e.printStackTrace();
        }

        String contentType = "application/octet-stream";
        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}