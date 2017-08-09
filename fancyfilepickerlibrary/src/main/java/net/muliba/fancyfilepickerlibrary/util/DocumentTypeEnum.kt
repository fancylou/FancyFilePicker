package net.muliba.fancyfilepickerlibrary.util

/**
 * Created by fancy on 2017/8/8.
 * Copyright © 2017 O2. All rights reserved.
 */

enum class DocumentTypeEnum(val label:String, val value:String) {
    TXT("TXT", "text/plain"),
    XML("XML", "text/xml"),
    HTML("HTML", "text/html"),
    WORD("WORD", "application/msword"),
    EXCEL("EXCEL", "application/vnd.ms-excel"),
    PPT("PPT", "application/vnd.ms-powerpoin"),
    PDF("PDF", "application/pdf");


}