package com.juanvictordev.megabyte.service;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.springframework.stereotype.Service;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectsArgs;
import io.minio.Result;
import io.minio.messages.DeleteError;
import io.minio.messages.DeleteObject;


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
      throw new RuntimeException("Erro ao fazer upload do objeto", e);
    }
  }

  //METODO PARA DOWNLOAD DA DESCRICAO DO PRODUTO NO MIN.IO
  public String download(String descricao) {
    try (
      InputStream obj = getMinioClient().getObject(GetObjectArgs
      .builder()
      .bucket("repository")
      .object(descricao)
      .build())
    ) {

      byte[] objByte = obj.readAllBytes();
      return new String(objByte);

    } catch (Exception e) {
      throw new RuntimeException("Erro ao baixar a descrição do produto", e);
    }
  }

  //METODO PARA DELETAR IMAGEM E DESCRICAO NO MIN.IO
  public void delete(String objImagem, String objDescricao){
    List<DeleteObject> objects = new LinkedList<>();
    objects.add(new DeleteObject(objImagem));
    objects.add(new DeleteObject(objDescricao));

    try {
      getMinioClient().removeObjects(RemoveObjectsArgs
      .builder()
      .bucket("repository")
      .objects(objects)
      .build());
    } catch (Exception e) {
      throw new RuntimeException("Erro ao deletar objeto", e);
    }
 }
}