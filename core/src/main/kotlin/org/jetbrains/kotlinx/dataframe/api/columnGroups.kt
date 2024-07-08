package org.jetbrains.kotlinx.dataframe.api

import org.jetbrains.kotlinx.dataframe.DataFrame
import org.jetbrains.kotlinx.dataframe.DataRow
import org.jetbrains.kotlinx.dataframe.annotations.DataSchema
import org.jetbrains.kotlinx.dataframe.columns.ColumnGroup
import org.jetbrains.kotlinx.dataframe.columns.ColumnSet
import org.jetbrains.kotlinx.dataframe.columns.FrameColumn
import org.jetbrains.kotlinx.dataframe.impl.schema.getSchema
import kotlin.reflect.KClass

public inline fun <reified T> ColumnsSelectionDsl<*>.columnGroups(withSchema: DataFrame<T>): ColumnSet<DataRow<T>> = cols {
    val schema2 = (it.data as? ColumnGroup<*>)?.schema()
    schema2?.let { withSchema.compileTimeSchema().compare(schema2).isSuperOrEqual() } ?: false
}.cast()

public inline fun <reified T> ColumnsSelectionDsl<*>.columnGroups(): ColumnSet<DataRow<T>> = columnGroups(T::class)

@PublishedApi
internal fun <T> ColumnsSelectionDsl<*>.columnGroups(klass: KClass<*>): ColumnSet<DataRow<T>> = cols {
    val actualSchema = (it.data as? ColumnGroup<*>)?.schema()
    actualSchema?.let { getSchema(klass).compare(actualSchema).isSuperOrEqual() } ?: false
}.cast()

public inline fun <reified T> ColumnsSelectionDsl<*>.frameColumns(withSchema: DataFrame<T>): ColumnSet<DataFrame<T>> = cols {
    val schema2 = (it.data as? FrameColumn<*>)?.schema?.value
    schema2?.let { withSchema.compileTimeSchema().compare(schema2).isSuperOrEqual() } ?: false
}.cast()

public inline fun <reified T> ColumnsSelectionDsl<*>.frameColumns(): ColumnSet<DataFrame<T>> = frameColumns(T::class)

@PublishedApi
internal fun <T> ColumnsSelectionDsl<*>.frameColumns(klass: KClass<*>): ColumnSet<DataFrame<T>> = cols {
    val schema2 = (it.data as? FrameColumn<*>)?.schema?.value
    schema2?.let { getSchema(klass).compare(schema2).isSuperOrEqual() } ?: false
}.cast()

public data class A(val a: String)

@DataSchema
public interface Ab {
    public val a: Int
}

public fun main() {
    val df = dataFrameOf("a")(1, 2, 3, 4, 5).groupBy("a").toDataFrame()
    df.print()
    df.schema().print()

    val groups = dataFrameOf("a")(1, 2, 3, 4, 5).cast<Ab>()
        .pivot { expr { "col${it.a}" } }
        .with { it }
        .toDataFrame()

    groups.schema().print()

    val asFrame = groups
        .convert { columnGroups<Ab>() }
        .asFrame { it.add("c") { a + 1 } }

    asFrame.print()

    val sample = DataFrame.emptyOf<Ab>()

    val asFrame1 = groups
        .convert { columnGroups(withSchema = sample) }
        .asFrame { it.add("c") { a + 1 } }

    require(asFrame == asFrame1)

    val with = df
        .convert { frameColumns<Ab>() }
        .with { it.add("c") { a + 1 } }

    val with1 = df
        .convert { frameColumns(withSchema = sample) }
        .with { it.add("c") { a + 1 } }

    require(with == with1)

    with.print()
}
