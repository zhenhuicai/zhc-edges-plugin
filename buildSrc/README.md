# Plugins

[English](./README.md) | [简体中文](README_cn.md)

The following plugins are defined in the  [`build.gradle`](build.gradle)  file.

## `hope.optional`

### Overview

This plugin introduces support for Maven-style **optional dependencies** by creating a new configuration
named `optional`. Dependencies added to this configuration are included in the project’s **compile** and **runtime
classpaths**, but they do **not propagate** to dependent projects.

This behavior is particularly useful when you want to include certain dependencies for internal use without forcing
downstream consumers to include them unnecessarily.

### Usage

When using the shared template file [`spring.lib.gradle`](../gradle/spring.lib.gradle), apply the plugin as follows:

```groovy
apply plugin: "hope.optional"
//or use it standalone:
//id "hope.optional"

optional(libs.spring.context)
```

### Origin

This concept was inspired by Spring Framework’s implementation:
[OptionalDependenciesPlugin](https://github.com/spring-projects/spring-framework/blob/main/buildSrc/src/main/java/org/springframework/build/optional/OptionalDependenciesPlugin.java)

## `hope.router`

### Overview

This plugin automates the generation of **frontend routing structures** by scanning the source files in a
designated `pages` directory.

It eliminates the need for manual route configuration by detecting page components and dynamically generating a
structured routing file (e.g., `router.js` or `routes.ts`) based on the folder hierarchy.

### Documentation

For usage examples and detailed configuration instructions, please refer to the frontend documentation:

👉 [Vue Router Integration Guide](https://apihug.github.io/docs/ui/002_vue#router)

Sure! Here's an optimized version of your text while preserving the original style and meaning:

## `hope.rename`

This plugin helps rename package names. As Apihug maintains consistency between proto and implementation package
names,  
renaming packages can be a painful process. This plugin aims to relieve that burden.

- `-DpkgRename` – Specifies how to rename the package from old to new; format: `{old}-{new}`  
  Example: `com.abc-com.xyz`
- `-DskipModules` – Comma-separated list of modules to skip  
  Example: `module-a,module-b`

⚠️ **Important:** This plugin makes significant changes. Please BACKUP your project before execution.  
The results may still require some manual adjustments to ensure full consistency.


