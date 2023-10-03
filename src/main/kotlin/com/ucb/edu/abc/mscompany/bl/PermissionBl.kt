package com.ucb.edu.abc.mscompany.bl

import com.ucb.edu.abc.mscompany.entity.PermissionEntity
import org.springframework.stereotype.Service

@Service
class PermissionBl {


    fun createNewPermission(permissionEntity: PermissionEntity){
        if(!permissionEntity.isComplete()){
            return null
        }
        if(permissionEntity.roleId == 0){ // role for FOUNDERS

        }
    }


}