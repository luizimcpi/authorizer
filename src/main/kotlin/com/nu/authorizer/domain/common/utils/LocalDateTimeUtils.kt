package com.nu.authorizer.domain.common.utils

import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import kotlin.math.abs

object LocalDateTimeUtils {
    fun getDifferenceInMinutes(firstDate: LocalDateTime, lastDate: LocalDateTime): Long {
        return abs(ChronoUnit.MINUTES.between(firstDate, lastDate))
    }
}
