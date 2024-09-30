package com.example.samsungclockclone.domain.ext

import com.example.samsungclockclone.domain.model.AlarmOrder
import com.example.samsungclockclone.framework.utils.strings

fun AlarmOrder.toStringRes(): Int {
    return when (this) {
        AlarmOrder.DEFAULT -> strings.alarm_order_default
        AlarmOrder.ALARM_TIME_ORDER -> strings.alarm_order_alarm_time_order
        AlarmOrder.CUSTOM_ORDER -> strings.alarm_order_custom_order
    }
}