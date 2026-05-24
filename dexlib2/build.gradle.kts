plugins {
    `java-library`
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
    withSourcesJar()
}

dependencies {
    implementation("com.google.code.findbugs:jsr305:3.0.2")
    implementation("com.google.guava:guava:27.1-android")
}
