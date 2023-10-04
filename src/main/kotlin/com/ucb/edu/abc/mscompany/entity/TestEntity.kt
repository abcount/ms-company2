package com.ucb.edu.abc.mscompany.entity

import lombok.AllArgsConstructor
import lombok.Data
import lombok.NoArgsConstructor

@Data
@AllArgsConstructor
@NoArgsConstructor
data class TestEntity (
    var param1: Int,
    var param2: Int
) {
}