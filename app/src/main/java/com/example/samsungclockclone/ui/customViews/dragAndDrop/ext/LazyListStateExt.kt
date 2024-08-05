package com.example.samsungclockclone.ui.customViews.dragAndDrop.ext

import androidx.compose.foundation.lazy.LazyListItemInfo
import androidx.compose.foundation.lazy.LazyListState
import com.example.samsungclockclone.ui.customViews.dragAndDrop.Index

fun LazyListState.getLazyListItemInfo(index: Index): LazyListItemInfo {
    return this.layoutInfo.visibleItemsInfo[index - this.layoutInfo.visibleItemsInfo.first().index]
}