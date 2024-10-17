package com.sambavadekar.kmp.nsexceptionkt.core

import platform.Foundation.NSException

public interface CrashLogger {
    public fun requiresMergedException(): Boolean
    public fun reportException(exceptionBreadCrumbs: List<NSException>, exceptionPoint: NSException?)
}
