package com.ucb.edu.abc.mscompany.entity.pojos

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class FileEntity (
    var imageContent: ByteArray,
    var ownerId: Int = 0,
    var categoryOwner: String = "",
    var extensionFile: String = "",
    var uuidFile: String=""
){
}