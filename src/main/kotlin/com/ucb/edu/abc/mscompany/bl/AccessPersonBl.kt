package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.dao.AccessPersonDao
import com.ucb.edu.abc.mscompany.dto.response.KeycloakUserDto
import com.ucb.edu.abc.mscompany.dto.response.PersonInfoDto
import com.ucb.edu.abc.mscompany.entity.AccessPersonEntity
import com.ucb.edu.abc.mscompany.enums.UserAbcCategory
import com.ucb.edu.abc.mscompany.exception.UserNotFoundException
import lombok.AllArgsConstructor
import lombok.NoArgsConstructor
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.sql.Date
import java.sql.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Service
@AllArgsConstructor
@NoArgsConstructor
class AccessPersonBl @Autowired constructor(
    private val keycloakBl: KeycloakBl,
    private val accessPersonDao: AccessPersonDao,
    private val imageService: ImageService
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


    fun getAccessPersonInformationById(id: Int): AccessPersonEntity?{
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
            logger.error(ex2.message)
            return null;
        }
    }
    fun getAccessPersonInfoAsUserByUsernameOrEmail(searched: String, limit:Int): List<PersonInfoDto> {
        val listOfPossible = accessPersonDao.findUserByUsernameOrEmail("$searched%", limit)
            ?: throw Exception("Null list");
        return listOfPossible.map { itemAp ->
            PersonInfoDto(
                email = itemAp.email,
                id = itemAp.accessPersonId.toInt(),
                userName = itemAp.username,
                fullName = itemAp.firstName + " "+ itemAp.lastName,
                imagePath = imageService.getImageForUser(itemAp.accessPersonId.toInt()) ?: "" //  "http://localhost:8080/users/1/images/profile"
            )
        }
    }

    private fun convertUnixTimestampToLocalDate(timestamp: Long, timeZoneId: String): LocalDate {
        val instant = Instant.ofEpochSecond(timestamp)
        val zoneId = ZoneId.of(timeZoneId)
        val zonedDateTime = instant.atZone(zoneId)
        return zonedDateTime.toLocalDate()
    }

    private fun factoryAccessPersonEntity(userKc: KeycloakUserDto): AccessPersonEntity {
        val apE = AccessPersonEntity()
        apE.username = userKc.username;
        //apE.lastName = userKc.lastName;
        //apE.firstName = userKc.firstName;
        apE.email = userKc.email!!;
        val stamp = Timestamp(userKc.createdTimestamp)
        apE.dateCreation = Date(stamp.time)
        // Atts: attributes={no_identify=[], ext_no_fono=[], ext_no_identify=[], address=[], birthdate=[2001-12-23], country_identify=[], type_count=[on], last_name=[], no_fono=[], first_name=[], gender_person=[]},

        if (userKc.attributes != null){
            if( !userKc.attributes!!["birthday"].isNullOrEmpty()){
                apE.birthday = LocalDate.parse(userKc.attributes!!["birthday"]!![0], DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            }
            if( !userKc.attributes!!["last_name"].isNullOrEmpty()){
                apE.lastName = userKc.attributes!!["last_name"]!![0]
            }
            if( !userKc.attributes!!["first_name"].isNullOrEmpty()){
                apE.firstName = userKc.attributes!!["first_name"]!![0]
            }
        }
        if(userKc.enabled == true){
            apE.diccCategory = UserAbcCategory.ACTIVE.name;
        }else{
            apE.diccCategory = UserAbcCategory.DISABLED.name;
        }
        apE.userUuid = userKc.id
        return apE
    }
    private fun createAccessPersonWithDataKeycloak(userUuidFromToken:String): AccessPersonEntity {
        logger.info("#createAccessPersonWithDataKeycloak Creating user requesting keycloak ")
        val userKc = keycloakBl.getUserInfoFromKeycloak(userUuidFromToken)
        val accessPersonEntity = factoryAccessPersonEntity(userKc)
        // save in db
        accessPersonDao.save(accessPersonEntity)

        return accessPersonEntity
    }
}
