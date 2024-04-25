package com.sambavadekar.kmp.nsexceptionkt.core

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.UnsafeNumber
import kotlinx.cinterop.convert
import platform.Foundation.NSException
import platform.Foundation.NSNumber
import platform.darwin.NSUInteger
import kotlin.experimental.ExperimentalObjCName
import kotlin.reflect.KClass

/**
 * Registers a [reporter] used to report unhandled Kotlin exceptions.
 *
 * The unhandled exception hook is wrapped such that the unhandled exception is reported
 * before the currently set unhandled exception hook is invoked.
 * Note: once the unhandled exception hook returns the program will be terminated.
 *
 * @see setUnhandledExceptionHook
 * @see terminateWithUnhandledException
 */
@OptIn(ExperimentalForeignApi::class, ExperimentalObjCName::class)
public fun addReporter(
    @ObjCName(swiftName = "_") reporter: CrashLogger
): Unit = wrapUnhandledExceptionHook { throwable ->
    val requiresMergedException = reporter.requiresMergedException()
    val exceptions = mutableListOf(throwable.asNSException(requiresMergedException))
    if (!requiresMergedException) {
        exceptions.addAll(throwable.causes.map { it.asNSException() })
    }
    reporter.reportException(exceptions.drop(1), exceptions.firstOrNull())
}

/**
 * Returns a [NSException] representing `this` [Throwable].
 * If [appendCausedBy] is `true` then the name, message and stack trace
 * of the [causes][Throwable.cause] will be appended, else causes are ignored.
 */
@InternalNSExceptionKtApi
@OptIn(UnsafeNumber::class, ExperimentalForeignApi::class)
public fun Throwable.asNSException(appendCausedBy: Boolean = false): NSException {
    val returnAddresses = getFilteredStackTraceAddresses().let { addresses ->
        if (!appendCausedBy) return@let addresses
        addresses.toMutableList().apply {
            for (cause in causes) {
                addAll(cause.getFilteredStackTraceAddresses(true, addresses))
            }
        }
    }.map {
        NSNumber(unsignedInteger = it.convert<NSUInteger>())
    }
    return ThrowableNSException(name, getReason(appendCausedBy), returnAddresses)
}

/**
 * Returns the [qualifiedName][KClass.qualifiedName] or [simpleName][KClass.simpleName] of `this` throwable.
 * If both are `null` then "Throwable" is returned.
 */
internal val Throwable.name: String
    get() = this::class.qualifiedName ?: this::class.simpleName ?: "Throwable"

/**
 * Returns the [message][Throwable.message] of this throwable.
 * If [appendCausedBy] is `true` then caused by lines with the format
 * "Caused by: $[name]: $[message][Throwable.message]" will be appended.
 */
internal fun Throwable.getReason(appendCausedBy: Boolean = false): String? {
    if (!appendCausedBy) return message
    return buildString {
        message?.let(::append)
        for (cause in causes) {
            if (isNotEmpty()) appendLine()
            append("Caused by: ")
            append(cause.name)
            cause.message?.let { append(": $it") }
        }
    }.takeIf { it.isNotEmpty() }
}

internal class ThrowableNSException(
    name: String,
    reason: String?,
    private val returnAddresses: List<NSNumber>
): NSException(name, reason, null) {
    override fun callStackReturnAddresses(): List<NSNumber> = returnAddresses
}
