# Slate

Making UHC plugins easy

## Why slate

When you think about it, most UHC game modes work in a very similar way.
Slate takes care of stuff like game management, world generation, sidebars and such, so you don't have to.
It allows you to focus on your code without being distracted by boilerplate code.

## How to use

To use Slate in your projects, you first need to run these commands:

```shell
git clone git@github.com:JanotLeLapin/slate
cd slate

./gradlew build
./gradlew publishToMavenLocal
```

You can now use Slate in your plugins:

```groovy
repositories {
    mavenLocal()
}

dependencies {
    implementation 'io.github.janotlelapin.slate:slate-api:1.0-SNAPSHOT'
}
```

For your plugin to work on any Spigot based server, the server needs the Slate plugin,
which you can find in `slate/build/libs/slate-plugin-1.0-SNAPSHOT.jar`
