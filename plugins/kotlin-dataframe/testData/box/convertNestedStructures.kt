import org.jetbrains.kotlinx.dataframe.*
import org.jetbrains.kotlinx.dataframe.annotations.*
import org.jetbrains.kotlinx.dataframe.api.*
import org.jetbrains.kotlinx.dataframe.io.*

fun box(): String {
    val df = listOf(Record("112", 42, listOf(Nested(listOf(3.0))))).toDataFrame(maxDepth = 1)

    df.group { nested }.into("group").convert { colsAtAnyDepth().frameCols() }.with { it.remove { all().colsOf<List<*>>() } }.compareSchemas()
    return "OK"
}
