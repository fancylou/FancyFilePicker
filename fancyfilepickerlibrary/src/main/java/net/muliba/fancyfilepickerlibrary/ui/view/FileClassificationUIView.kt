package net.muliba.fancyfilepickerlibrary.ui.view

import android.content.Context

/**
 * Created by fancyLou on 2018/3/15.
 * Copyright © 2018 O2. All rights reserved.
 */
interface FileClassificationUIView {
    fun contextInstance(): Context
    fun returnItems(items: ArrayList<net.muliba.fancyfilepickerlibrary.model.DataSource>)
}