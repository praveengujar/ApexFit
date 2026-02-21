pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Zyva"

// Shared KMP module
include(":shared")
project(":shared").projectDir = file("../shared")

// App module
include(":app")

// Core modules
include(":core:model")
include(":core:engine")
include(":core:config")
include(":core:data")
include(":core:healthconnect")
include(":core:domain")
include(":core:designsystem")
include(":core:background")
include(":core:notifications")

// Feature modules
include(":feature:home")
include(":feature:recovery")
include(":feature:sleep")
include(":feature:strain")
include(":feature:journal")
include(":feature:activity")
include(":feature:trends")
include(":feature:settings")
include(":feature:onboarding")
include(":feature:longevity")
include(":feature:myplan")
include(":feature:coach")
include(":feature:profile")
