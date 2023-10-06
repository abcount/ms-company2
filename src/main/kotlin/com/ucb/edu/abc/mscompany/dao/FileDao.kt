package com.ucb.edu.abc.mscompany.dao

import com.ucb.edu.abc.mscompany.entity.pojos.FileEntity
import org.apache.ibatis.annotations.Mapper
import org.apache.ibatis.annotations.Select
import org.springframework.stereotype.Component

@Mapper
@Component
interface FileDao {
    @Select("SELECT * FROM images_for_all WHERE " +
            " owner_id = #{ownerId} " +
            "AND category_owner = #{categoryOwner};")
    fun getImageByIdAndCategory(ownerId: Int, categoryOwner: String): FileEntity

    @Select("INSERT INTO images_for_all " +
            "(image_content, owner_id, category_owner, extension_file, uuid_file ) " +
            " VALUES " +
            " (#{imageContent}, #{ownerId}, #{categoryOwner}, #{extensionFile}, #{uuidFile});")
    fun createImage(fileEntity: FileEntity)
}
