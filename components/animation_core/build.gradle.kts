plugins { `java-library` }

dependencies {
    compileOnly(libs.jsr305)
    implementation(libs.gson)
    implementation(libs.guava)
    implementation(libs.joml)
    implementation(libs.slf4j)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testRuntimeOnly(libs.junit.launcher)
}
