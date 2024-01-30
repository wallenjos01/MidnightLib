# MidnightLib

A general use Java library including Modules, Events, Registries, and some basic data types

## Usage
Add the following to your `build.gradle.kts` file:
```
repositories {
    maven("https://maven.wallentines.org/")
}

dependencies {
    compileOnly("org.wallentines:midnightlib:1.5.0-SNAPSHOT")
}
```

## Features
### Registries
- A registry is a data type which contains key-value pairs
- There are two types by default: `Registry` and `StringRegistry`
  - The `Registry` type expects keys of type `Identifier`
  - The `StringRegistry` type expects keys of type `String`
- Registries can be frozen using `RegistryBase::freeze()`, preventing them from being modified
- Registries can be configured to prevent the same value from being registered twice

### Modules
- MidnightLib has a system which allows you to create configurable modules which can be enabled or disabled at runtime.
- To implement this, start with a `ModuleManager`
  - A `ModuleManager` object is responsible for loading and unloading modules, and managing dependencies between them.
  - `ModuleManager` expects a template argument `T`, which defines the type of object which will be passed to all
modules when they are loaded.
- Next, create a `Registry` for `ModuleInfo` objects, and populate it with `ModuleInfo` objects
  - `ModuleInfo` objects define suppliers, default configurations, and dependencies for `Module` objects
- Pass the registry to `ModuleManger::loadAll`, along with a `ConfigSection` containing module configurations, and a
data value of type `T`
- `ModuleManager::loadAll` returns the number of modules successfully loaded

### Events
- MidnightLib has an Event system
- Global events can be fired using `Event.invoke()` and listened to using `Event.register`
- Specialized events can be implemented manually using a `HandlerList` object
  - Use `HandlerList::invoke` to fire events
  - Use `HandlerList::register` to register events

### Data Types
- `Either<A,B>` - Contains only one of the two template types
- `Singleton<T>` - Contains an instance of the template type
  - The default type cannot be reset once populated. Use `ResettableSingleton<T>` in the case you need to reset one
  - Use `DefaultedSingleton<T>` in the case where a default value should be specified before its populated
- `RandomizedQueue<T>` - A queue which randomizes its inputs as they are added
- `RandomizedLoopingQueue<T>` - A randomized queue which contains a set list of values.
  - When the queue runs out, it will be refilled in a random order from the original list of values
- `SortedCollection<T>` - A collection which automatically sorts its contents as they are added
