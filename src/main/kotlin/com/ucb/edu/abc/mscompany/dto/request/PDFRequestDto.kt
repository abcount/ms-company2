package com.ucb.edu.abc.mscompany.dto.request

data class PDFRequestDto(
    var footerHtmlTemplate: String,
    var headerHtmlTemplate: String,
    var htmlTemplate: String,
    var model: Any,
    var options: OptionDto,
    var templateEngine: String
){
    override fun toString(): String {
        return "PDFRequestDto(footerHtmlTemplate='$footerHtmlTemplate', headerHtmlTemplate='$headerHtmlTemplate', htmlTemplate='$htmlTemplate', model=$model, options=$options, templateEngine='$templateEngine')"
    }

    constructor() : this("" , "" , "" , Any() , OptionDto() , "")
}

/*
* mapOf(
            "landscape" to false,
            "pageFormat" to "Letter",
            "margin" to mapOf(
                "top" to 25,
                "bottom" to 25,
                "left" to 25,
                "right" to 25
            )
        ),*/
data class OptionDto(
    var landsacpe: Boolean,
    var pageFormat: String,
    var margin: MarginDto
){
    override fun toString(): String {
        return "OptionDto(landsacpe=$landsacpe, pageFormat='$pageFormat', margin=$margin)"
    }

    constructor() : this(false , "" , MarginDto())
}

data class MarginDto(
    var top: Int,
    var bottom: Int,
    var left: Int,
    var right: Int
){
    override fun toString(): String {
        return "MarginDto(top=$top, bottom=$bottom, left=$left, right=$right)"
    }

    constructor() : this(0 , 0 , 0 , 0)
}
