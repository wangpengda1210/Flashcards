import java.util.Scanner

fun main(args: Array<String>) {
    val scanner = Scanner(System.`in`)
    var sum = 0
    for (digit in scanner.next()) sum += digit.toString().toInt()
    print(sum)
}