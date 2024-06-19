package com.juanvictordev.megabyte.service;

import java.io.BufferedInputStream;
import java.io.InputStream;
import org.springframework.stereotype.Service;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;


@Service
public class MinioService {


  //--usar variavel de ambiente pra setar as coisas
  private MinioClient getMinioClient(){
    MinioClient minioClient = MinioClient
    .builder()
    .endpoint("http://127.0.0.1:9000")
    .credentials("minioadmin", "minioadmin")
    .build();

    return minioClient;
  }

  //METODO PARA UPLOAD DE DADOS NO MIN.IO
  public void upload(InputStream inputStream, long objectSize, String objectName){
    try (BufferedInputStream input = new BufferedInputStream(inputStream)) {  
      getMinioClient().putObject(PutObjectArgs
      .builder()
      .bucket("repository")
      .object(objectName)
      .stream(input, objectSize, -1)
      .build());
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public String download(String desc) throws Exception{
    GetObjectResponse obj = getMinioClient().getObject(GetObjectArgs
    .builder()
    .bucket("repository")
    .object(desc)
    .build());

    return new String(obj.readAllBytes());
  }

}
