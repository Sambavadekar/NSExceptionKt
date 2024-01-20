// swift-tools-version: 5.5
// The swift-tools-version declares the minimum version of Swift required to build this package.

import PackageDescription

let package = Package(
    name: "NSExceptionKt",
    products: [
        .library(
            name: "NSExceptionKtCrashlytics",
            targets: ["NSExceptionKtCrashlytics"]
        )
    ],
    dependencies: [
        .package(
            url: "https://github.com/firebase/firebase-ios-sdk.git",
            "9.3.0"..<"11.0.0"
        )
    ],
    targets: [
        .target(
            name: "NSExceptionKtCoreObjC",
            path: "NSExceptionKtCoreObjC",
            publicHeadersPath: "."
        ),
        .target(
            name: "NSExceptionKtCrashlyticsObjC",
            path: "NSExceptionKtCrashlyticsObjC",
            publicHeadersPath: "."
        ),
        .target(
            name: "NSExceptionKtCrashlytics",
            dependencies: [
                .target(name: "NSExceptionKtCoreObjC"),
                .target(name: "NSExceptionKtCrashlyticsObjC"),
                .product(name: "FirebaseCrashlytics", package: "firebase-ios-sdk")
            ],
            path: "NSExceptionKtCrashlytics"
        )
    ]
)
