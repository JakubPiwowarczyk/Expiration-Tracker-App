package com.example.project1.model

import java.time.LocalDate
import java.util.UUID

data class Product(
    var name: String,
    var category: ProductCategory,
    var quantity: Int,
    var expirationDate: LocalDate
) {
    var status: ProductStatus = ProductStatus.Valid
    var id: UUID = UUID.randomUUID()

    init {
        updateStatus()
    }

    fun updateStatus() {
        status = if (expirationDate.isBefore(LocalDate.now())) {
            ProductStatus.Expired
        } else {
            ProductStatus.Valid
        }
    }
}
