package com.ucb.edu.abc.mscompany.bl

import io.minio.GetPresignedObjectUrlArgs
import io.minio.MinioClient
import io.minio.ObjectWriteResponse
import io.minio.PutObjectArgs
import io.minio.http.Method
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.ByteArrayInputStream
import java.util.UUID

@Service
class MinioBl @Autowired constructor(
    private val minioClient: MinioClient
){
    @Value("\${minio.bucket}")
    lateinit var bucketName: String

    private val logger = LoggerFactory.getLogger(this::class.java)

    suspend fun uploadFile(byteArray: ByteArray, name: String, contentType: String): String{
        try{
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val response: Deferred<ObjectWriteResponse> = coroutineScope.async {
                minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucketName)
                    .contentType(contentType)
                    .`object`(name)
                    .stream(ByteArrayInputStream(byteArray), byteArray.size.toLong(), -1)
                    .build() )
            }
            if(response.await().bucket() != bucketName){
                logger.error("Ocurrio un error al subir el archivo $name al bucket $bucketName")
                throw Exception("Ocurrio un error al subir el archivo $name al bucket $bucketName")
            }
            else {
                logger.info("Se subio el archivo $name al bucket $bucketName")
                return getPreSignedUrl(name)
            }
        }catch (ex: Exception){
            logger.error("Ocurrio un error al subir el archivo al bucket $bucketName")
            println(ex.message)
            throw Exception("Ocurrio un error al subir el archivo al bucket $bucketName")
        }
    }

    suspend fun getPreSignedUrl(filename: String): String{
        return try {
            val coroutineScope = CoroutineScope(Dispatchers.IO)
            val response: Deferred<String> = coroutineScope.async {
                minioClient.getPresignedObjectUrl(
                    GetPresignedObjectUrlArgs.builder()
                        .method(Method.GET)
                        .bucket(bucketName)
                        .`object`(filename)
                        .expiry(60 * 60 * 24) // one day
                        .build()
                )
            }
            return response.await()
        }catch (ex: Exception){
            logger.error("hubo un error ${ex.message}")
            throw Exception("hubo un error ${ex.message}")
        }
    }
}