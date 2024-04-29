plugins {
    alias(libs.plugins.kotlin.multiplatform)
    id(libs.plugins.maven.publish.get().pluginId)
}
val GITHUB_USER: String? by project
val GITHUB_TOKEN: String? by project
kotlin {
    explicitApi()
    jvmToolchain(11)

    listOf(
        macosX64(), macosArm64(),
        iosArm64(), iosX64(), iosSimulatorArm64(),
        watchosArm32(), watchosArm64(), watchosX64(), watchosSimulatorArm64(), watchosDeviceArm64(),
        tvosArm64(), tvosX64(), tvosSimulatorArm64()
    ).forEach {
        it.compilations.getByName("main") {
        }
    }

    sourceSets {
        all {
            languageSettings.optIn("com.sambavadekar.kmp.nsexceptionkt.core.InternalNSExceptionKtApi")
        }
        commonTest {
            dependencies {
                implementation(kotlin("test"))
            }
        }
    }
}
publishing {
    repositories {
        maven {
            setUrl("https://repo.repsy.io/mvn/sambavadekar/nsexceptionkit")
            credentials {
                username = GITHUB_USER
                password = GITHUB_TOKEN
            }
        }
    }
}
