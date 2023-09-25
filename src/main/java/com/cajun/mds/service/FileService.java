package com.cajun.mds.service;

import com.cajun.mds.domain.File;
import com.cajun.mds.domain.Item;
import com.cajun.mds.dto.FileDto;
import com.cajun.mds.repository.FileRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileService {

    private final FileRepository fileRepository;

    @Value("${file.dir}")
    private String fileDir;

//    @Transactional
//    public Long upload(FileDto.Request request) {
//        return fileRepository.save(request.toEntity()).getFilePk();
//    }

    @Transactional
    public Long saveFile(FileDto.Request request) throws IOException {
        MultipartFile file = request.getMultipartFile();
        if(file.isEmpty()) return null;

        String origName = file.getOriginalFilename();
        String uuid = UUID.randomUUID().toString();
        String extension = origName.substring(origName.lastIndexOf(".")); // 확장자 추출
        String savedName = uuid + extension;
        String savedPath = fileDir + savedName;

        Item item = Item.builder()
                .itemPk(request.getItemPk())
                .build();

        File f = File.builder()
                .fileName(savedName)
                .filePath(savedPath)
                .originalFileName(origName)
                .type(request.getType())
                .item(item)
                .build();

        file.transferTo(new java.io.File(savedPath));

        return fileRepository.save(f).getFilePk();
    }
}