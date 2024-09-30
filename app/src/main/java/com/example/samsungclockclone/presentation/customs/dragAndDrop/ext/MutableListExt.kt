package com.example.samsungclockclone.presentation.customs.dragAndDrop.ext

import com.example.samsungclockclone.presentation.customs.dragAndDrop.Index

fun <T> MutableList<T>.move(fromIndex: Index, toIndex: Index) {
    if (fromIndex == toIndex) return

    val element = this.removeAt(fromIndex)
    this.add(toIndex, element)
}