package com.nu.authorizer.domain.common.utils

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.time.LocalDateTime

class LocalDateTimeUtilsTest {

    @Test
    fun `when receive valid first date and last date in the same hour and same day should return difference in minutes`() {
        val firstDate = LocalDateTime.of(2020, 6, 1, 14, 45, 45)
        val lastDate = LocalDateTime.of(2020, 6, 1, 14, 41, 52)
        assertEquals(3, LocalDateTimeUtils.getDifferenceInMinutes(firstDate, lastDate))
    }

    @Test
    fun `when receive reverse valid first date and last date in the same hour and same day should return difference in minutes`() {
        val firstDate = LocalDateTime.of(2020, 6, 1, 14, 45, 45)
        val lastDate = LocalDateTime.of(2020, 6, 1, 14, 41, 52)
        assertEquals(3, LocalDateTimeUtils.getDifferenceInMinutes(lastDate, firstDate))
    }

    @Test
    fun `when receive valid first date and last date in the same hour but different day should return difference in minutes`() {
        val firstDate = LocalDateTime.of(2020, 6, 1, 14, 45, 45)
        val lastDate = LocalDateTime.of(2020, 6, 2, 14, 45, 45)
        assertEquals(1440, LocalDateTimeUtils.getDifferenceInMinutes(firstDate, lastDate))
    }
}
