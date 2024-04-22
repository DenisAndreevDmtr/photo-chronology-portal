package com.andersen.pc.portal.service;

import com.amazonaws.HttpMethod;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.DeleteObjectsRequest;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.andersen.pc.common.exception.DbObjectConflictException;
import com.andersen.pc.portal.utils.FileConverter;
import com.andersen.pc.portal.utils.KeyGenerator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.andersen.pc.common.constant.Constant.Errors.ERROR_OCCURRED_WHILE_SAVING_PHOTO;

@Service
public class S3PhotoService {

    private final AmazonS3 s3Client;

    @Value("${aws.s3.bucketName}")
    private String bucketName;

    @Value("${aws.s3.folderName}")
    private String folderName;

    @Value("${aws.s3.expiration}")
    private Integer expirationMilliseconds;

    public S3PhotoService(AmazonS3 s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadPhotoToS3Bucket(MultipartFile multipartFile, Long userId) {
        try {
            File photoFile = FileConverter.convertMultiPartFileToFile(multipartFile);

            String key = KeyGenerator.generateKeyForPhoto(folderName, photoFile.getName(), userId);

            PutObjectRequest putRequest = new PutObjectRequest(bucketName, key, photoFile);
            s3Client.putObject(putRequest);
            photoFile.delete();
            return key;
        } catch (Exception IOException) {
            throw new DbObjectConflictException(ERROR_OCCURRED_WHILE_SAVING_PHOTO);
        }
    }

    public String generatePreSignedUrl(String key) {
        Date expiration = new Date();
        expiration.setTime(expiration.getTime() + expirationMilliseconds);

        GeneratePresignedUrlRequest generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, key)
                .withMethod(HttpMethod.GET)
                .withExpiration(expiration);
        URL url = s3Client.generatePresignedUrl(generatePresignedUrlRequest);

        return url.toString();
    }

    public void deletePhotoByKey(String key) {
        DeleteObjectRequest deleteRequest = new DeleteObjectRequest(bucketName, key);
        s3Client.deleteObject(deleteRequest);
    }

    public void deletePhotoByKeyList(List<String> keys) {
        List<DeleteObjectsRequest.KeyVersion> objectsToDelete = new ArrayList<>();
        keys.forEach(key -> objectsToDelete.add(new DeleteObjectsRequest.KeyVersion(key)));

        DeleteObjectsRequest deleteRequest = new DeleteObjectsRequest(bucketName)
                .withKeys(objectsToDelete);
        s3Client.deleteObjects(deleteRequest);
    }
}
