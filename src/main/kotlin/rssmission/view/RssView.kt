package rssmission.view

class RssView {
    fun printContent(content: String) {
        println(content)
    }

    fun readInputContent(): String {
        return readLine() ?: ""
    }
}
