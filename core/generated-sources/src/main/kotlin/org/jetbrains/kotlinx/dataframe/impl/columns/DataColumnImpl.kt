package org.jetbrains.kotlinx.dataframe.impl.columns

import org.jetbrains.kotlinx.dataframe.ColumnDataHolder
import org.jetbrains.kotlinx.dataframe.DataColumn
import org.jetbrains.kotlinx.dataframe.api.dataFrameOf
import kotlin.reflect.KType

internal abstract class DataColumnImpl<T>(
    protected val values: ColumnDataHolder<T>,
    val name: String,
    val type: KType
) : DataColumn<T>, DataColumnInternal<T> {

    protected val distinct
        get() = values.distinct

    override fun name() = name

    override fun values(): List<T> = values.toList()

    override fun type() = type

    override fun toSet() = distinct.value

    override fun contains(value: T): Boolean =
        if (distinct.isInitialized()) distinct.value.contains(value) else values.contains(value)

    override fun toString() = dataFrameOf(this).toString() // "${name()}: $type"

    override fun countDistinct() = toSet().size

    override fun get(index: Int) = values[index]

    override fun size() = values.size

    override fun equals(other: Any?) = checkEquals(other)

    private val hashCode by lazy { getHashCode() }

    override fun hashCode() = hashCode

    override operator fun get(range: IntRange) =
        createWithValues(values[range])

    protected abstract fun createWithValues(values: List<T>, hasNulls: Boolean? = null): DataColumn<T>
}
