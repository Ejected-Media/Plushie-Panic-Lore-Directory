// Place this inside your app-level build.gradle.kts (e.g., app/build.gradle.kts)

tasks.register<Exec>("buildGoWasm") {
    group = "build"
    description = "Compiles the Go Plushie Panic engine to WebAssembly and copies it to Android assets"

    // Path to your Go backend source directory containing main.go
    workingDir = file("$rootDir/../wasm-backend") 

    // Set environment variables required for WASM target compilation
    environment("GOOS", "js")
    environment("GOARCH", "wasm")

    // Execute the Go build command
    commandLine("go", "build", "-o", "$rootDir/app/src/main/assets/plushie_engine.wasm", "main.go")
}

// Ensure the Go WASM binary is built automatically whenever the Android app is compiled
tasks.named("preBuild") {
    dependsOn("buildGoWasm")
}
