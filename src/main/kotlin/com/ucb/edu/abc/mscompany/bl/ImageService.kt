package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.FileDao
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ImageService @Autowired constructor(
    private val fileDao: FileDao
) {
    @Value("\${server.port}")
    lateinit var port: String

    fun getImageFormatCompany(id: Int): String? {
        val fileEntity = fileDao.getImageByIdAndCategorySimple(categoryOwner ="COMPANY-PROFILE-IMAGE", ownerId = id)
        if(fileEntity.isNullOrEmpty()){
            return null
        }
        return "http://localhost:$port/image/company/${id}"
    }

    fun getImageForUser(id:Int):String?{
        val fileEntity = fileDao.getImageByIdAndCategorySimple(categoryOwner ="USER-PROFILE-IMAGE", ownerId = id)
        if(fileEntity.isNullOrEmpty()){
            return null
        }
        return "http://localhost:$port/image/user/${id}"
    }

    fun getImage(cate: String, id: Int): ByteArray? {
        if(cate == "COMPANY"){
            val fileEntity =  fileDao.getImageByIdAndCategory(categoryOwner ="COMPANY-PROFILE-IMAGE", ownerId = id)
            if(fileEntity.isNullOrEmpty()){
                return null
            }
            return fileEntity[0].imageContent
        }
        if(cate == "USER"){
            val fileEntity =  fileDao.getImageByIdAndCategory(categoryOwner ="USER-PROFILE-IMAGE", ownerId = id)
            if(fileEntity.isNullOrEmpty()){
                return null
            }
            return fileEntity[0].imageContent
        }

        return null;
    }
}