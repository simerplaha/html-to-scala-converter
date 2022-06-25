package simer.html.converter

package object utils {
  def tripleQuote(string: String): String =
    string.trim match {
      case string if string.contains("\"") || string.contains("\n") || string.contains("\\") =>
        s"""\"\"\"$string\"\"\""""

      case string =>
        s""""$string""""
    }
}
