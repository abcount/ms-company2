package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.AccessPersonDao
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.exception.UserNotFoundException
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
@AllArgsConstructor
@NoArgsConstructor
class AccessPersonBl @Autowired constructor(
    private val keycloakBl: KeycloakBl,
    private val accessPersonDao: AccessPersonDao
) {

    private val logger: Logger = LoggerFactory.getLogger(this::class.java)
    fun getAccessPersonInformationByUuid(uuid: String):AccessPersonEntity?{
        try{
            return accessPersonDao.findByAccessPersonByUuid(uuid)
                .orElseThrow{ UserNotFoundException("user with uuid $uuid not found") }
        }catch (ex: UserNotFoundException) {
            return createAccessPersonWithDataKeycloak(uuid);
        }catch (ex2: Exception){
            return null;
        }
    }

    fun getAccessPersonInformationById(id: String): AccessPersonEntity?{
        try {
            val accessPersonEntity = accessPersonDao.findById(id.toInt())
                .orElseThrow{ UserNotFoundException ("access person with id $id dont found in local DB")}
            return accessPersonEntity;
        }catch (ex: Exception){
            return null;
        }
    }
    fun getAccessPersonInformationByToken(token: String): AccessPersonEntity? {
        val userUuidFromToken = keycloakBl.getKeycloakIdFromToken(token);
        try{

            val accessPerson = accessPersonDao.findByAccessPersonByUuid(userUuidFromToken!!)
                .orElseThrow{ UserNotFoundException("user with token $userUuidFromToken not found") }
            logger.info("#getAccessPersonInformationByToken accessPerson FOUND ${accessPerson.accessPersonId}")
            return accessPerson;
        } catch (ex: UserNotFoundException) {
            logger.error("#getAccessPersonInformationByToken User not found")
            return createAccessPersonWithDataKeycloak(userUuidFromToken!!);
        }catch (ex2: Exception){
            return null;
        }
    }
    private fun createAccessPersonWithDataKeycloak(userUuidFromToken:String): AccessPersonEntity {
        logger.info("#createAccessPersonWithDataKeycloak Creating user requesting keycloak ")
        val userKc = keycloakBl.getUserInfoFromKeycloak(userUuidFromToken)
        val accessPersonEntity = AccessPersonEntity()
        accessPersonEntity.email = userKc.email;
        accessPersonEntity.username = userKc.username;
        if( !userKc.attributes["birthday"].isNullOrEmpty()){
            accessPersonEntity.birthday = LocalDate.parse(userKc.attributes["birthday"]!![0], DateTimeFormatter.ofPattern("dd-MM-yyyy"));
        }
        accessPersonEntity.userUuid = userKc.id
        accessPersonEntity.diccCategory = "CREATED"
        accessPersonEntity.dateCreation = LocalDate.now()
        // save in db
        val res = accessPersonDao.save(accessPersonEntity)

        accessPersonEntity.accessPersonId = res.toLong();
        return accessPersonEntity
    }
}