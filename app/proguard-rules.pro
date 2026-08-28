# Game Builds release hardening.
# R8 is enabled by isMinifyEnabled=true in the release build type and will
# shrink, optimize and obfuscate classes and methods that are safe to rename.
# Keep line numbers for crash deobfuscation while hiding original source names.
-renamesourcefileattribute SourceFile
-keepattributes LineNumberTable
