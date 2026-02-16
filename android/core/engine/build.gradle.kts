plugins {
    alias(libs.plugins.kotlin.jvm)
}

dependencies {
    implementation(project(":core:model"))
    testImplementation(libs.junit)
    testImplementation(libs.kotlin.test)
}
