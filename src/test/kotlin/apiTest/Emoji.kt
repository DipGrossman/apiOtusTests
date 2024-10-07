package apiTest

data class Emoji(
    val name: String,
    val category: String,
    val group: String,
    val htmlCode: List<String>,
    val unicode: List<String>
)