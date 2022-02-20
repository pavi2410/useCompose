# useCompose

Headless @Composable hooks that drive UI logic. Inspired by React. 

[![](https://jitpack.io/v/pavi2410/useCompose.svg)](https://jitpack.io/#pavi2410/useCompose) [![CI](https://github.com/pavi2410/useCompose/actions/workflows/ci.yml/badge.svg)](https://github.com/pavi2410/useCompose/actions/workflows/ci.yml)

![carbon](https://user-images.githubusercontent.com/28837746/146000979-428e294d-425c-448b-a5e4-66110ab81100.png)

## Modules

### ⚛ react
- useState
- useEffect
- useContext
- useReducer

### 🪝 hooks
- useToggle

### 🕸 network
- useConnnectionStatus

## Installation

Step 1. Add the JitPack repository to your build file 

Add it in your root build.gradle at the end of repositories:
```gradle
repositories {
  ...
  maven {
    url = uri("https://jitpack.io")
  }
}
```

Step 2. Add the dependency
```gradle
dependencies {
  implementation("com.github.pavi2410.useCompose:<module_name>:<version>")
}
```

## Help Wanted

I want your help in making this library extensive such that this cover many of the commonly used hooks. Also, I want your help in building a KMP friendly library.

## License

[MIT](https://choosealicense.com/licenses/mit/)
