package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.FileDao
import com.ucb.edu.abc.mscompany.entity.pojos.FileEntity
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import kotlin.math.log

@Service
class ImageService @Autowired constructor(
    private val fileDao: FileDao,
    private val minioBl: MinioBl
) {
    private val logger = LoggerFactory.getLogger(this::class.java)
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
    fun getImageCompanySigned(companyId:Int): String? {
        logger.info("Request a company image signed companyId=$companyId")
        val fileEntity = fileDao.getImageByIdAndCategory(categoryOwner ="COMPANY-PROFILE-IMAGE", ownerId = companyId);
        if(fileEntity.isNullOrEmpty()){
            logger.error("Couldn't get file related to companyId=$companyId")
            return null
        }
        logger.info("Founded url signed with uuid= ${fileEntity[0].uuidFile}")

        return minioBl.getPreSignedUrlV2(fileEntity[0].uuidFile)
    }
}