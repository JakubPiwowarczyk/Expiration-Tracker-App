package com.example.project1.model

import java.io.Serializable
import java.util.UUID

sealed class FormType : Serializable {
    data object New : FormType() {
        private fun readResolve(): Any = New
    }

    data class Edit(val id: UUID) : FormType()
}