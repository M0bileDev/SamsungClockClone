package com.example.samsungclockclone.ui.customViews.dragAndDrop.ext

import com.example.samsungclockclone.ui.customViews.dragAndDrop.Index

fun <T> MutableList<T>.move(fromIndex: Index, toIndex: Index) {
    if (fromIndex == toIndex) return

    val element = this.removeAt(fromIndex)
    this.add(toIndex, element)
}