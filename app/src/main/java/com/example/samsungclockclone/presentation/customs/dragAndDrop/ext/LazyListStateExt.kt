package com.example.samsungclockclone.presentation.customs.dragAndDrop.ext

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import com.example.samsungclockclone.presentation.customs.dragAndDrop.Index

fun LazyListState.getLazyListItemInfo(index: Index): LazyListItemInfo? {
    return this.layoutInfo.visibleItemsInfo.getOrNull(index - this.layoutInfo.visibleItemsInfo.first().index)
}