package com.example.project1.model

import androidx.annotation.DrawableRes
import com.example.project1.R

enum class ProductStatus(@DrawableRes val statusIcon: Int) {
    Valid(R.drawable.ic_valid),
    Expired(R.drawable.ic_expired)
}