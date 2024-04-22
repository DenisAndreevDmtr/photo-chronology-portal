package com.andersen.pc.portal.utils;

import com.andersen.pc.common.exception.DbObjectConflictException;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

import static com.andersen.pc.common.constant.Constant.Errors.ERROR_OCCURRED_WHILE_SAVING_PHOTO;

@UtilityClass
public class FileConverter {

    public File convertMultiPartFileToFile(MultipartFile multipartFile) throws IOException {
        File file = new File(Objects.requireNonNull(multipartFile.getOriginalFilename()));
        try (OutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(multipartFile.getBytes());
        }
        return file;
    }
}