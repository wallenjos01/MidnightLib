package org.wallentines.midnightlib.config.serialization;

import org.wallentines.midnightlib.config.ConfigRegistry;
import org.wallentines.midnightlib.config.ConfigSection;

import java.util.*;
import java.util.function.Function;

@SuppressWarnings({"DuplicatedCode", "unused"})
public interface ConfigSerializer<T> extends Serializer<T, ConfigSection> {

    T deserialize(ConfigSection section);

    ConfigSection serialize(T object);

    default boolean canDeserialize(ConfigSection sec) {
        T val;
        try {
            val = deserialize(sec);
        } catch (Exception ex) {
            return false;
        }
        return val != null;
    }

    default Serializer<T, Object> toRaw() {

        return new Serializer<T, Object>() {
            @Override
            public Object serialize(T value) {
                return ConfigSerializer.this.serialize(value);
            }

            @Override
            public T deserialize(Object object) {

                if(!(object instanceof ConfigSection)) throw new IllegalArgumentException("Object must be a ConfigSection!");
                return ConfigSerializer.this.deserialize((ConfigSection) object);
            }

            @Override
            public boolean canDeserialize(Object object) {
                return object instanceof ConfigSection && ConfigSerializer.this.canDeserialize((ConfigSection) object);
            }
        };
    }


    static <T, R> Entry<T, R> entry(Class<T> clazz, String key, Function<R, T> getter) {

        return new SingleEntry<>(key, new ClassSerializer<>(clazz, ConfigRegistry.INSTANCE), getter);
    }

    static <T, R> Entry<T, R> entry(ConfigSerializer<T> serializer, String key, Function<R, T> getter) {
        return new SingleEntry<>(key, serializer.toRaw(), getter);
    }

    static <T, R> Entry<T, R> entry(InlineSerializer<T> serializer, String key, Function<R, T> getter) {
        return new SingleEntry<>(key, serializer.toRaw(), getter);
    }
    static <T, R> ListEntry<T, R> listEntry(Class<T> clazz, String key, Function<R, Collection<T>> getter) {

        return new ListEntry<>(key, new ClassSerializer<>(clazz, ConfigRegistry.INSTANCE), getter);
    }

    static <T, R> ListEntry<T, R> listEntry(ConfigSerializer<T> serializer, String key, Function<R, Collection<T>> getter) {

        return new ListEntry<>(key, serializer.toRaw(), getter);
    }

    static <T, R> ListEntry<T, R> listEntry(InlineSerializer<T> serializer, String key, Function<R, Collection<T>> getter) {

        return new ListEntry<>(key, serializer.toRaw(), getter);
    }

    static <T, R> MapEntry<String, T, R> mapEntry(Class<T> clazz, String key, Function<R, Map<String, T>> getter) {
        return new MapEntry<>(key, InlineSerializer.RAW, new ClassSerializer<>(clazz, ConfigRegistry.INSTANCE), getter);
    }

    static <T, R> MapEntry<String, T, R> mapEntry(ConfigSerializer<T> serializer, String key, Function<R, Map<String, T>> getter) {
        return new MapEntry<>(key, InlineSerializer.RAW, serializer.toRaw(), getter);
    }

    static <T, R> MapEntry<String, T, R> mapEntry(InlineSerializer<T> serializer, String key, Function<R, Map<String, T>> getter) {
        return new MapEntry<>(key, InlineSerializer.RAW, serializer.toRaw(), getter);
    }

    static <K, V, R> MapEntry<K, V, R> mapEntry(InlineSerializer<K> keySerializer, ConfigSerializer<V> valueSerializer, String key, Function<R, Map<K, V>> getter) {
        return new MapEntry<>(key, keySerializer, valueSerializer.toRaw(), getter);
    }

    static <K, V, R> MapEntry<K, V, R> mapEntry(InlineSerializer<K> keySerializer, InlineSerializer<V> valueSerializer, String key, Function<R, Map<K, V>> getter) {
        return new MapEntry<>(key, keySerializer, valueSerializer.toRaw(), getter);
    }

    static <T> ConfigSerializer<T> of(Function<T, ConfigSection> serialize, Function<ConfigSection, T> deserialize) {
        return new ConfigSerializer<T>() {
            @Override
            public T deserialize(ConfigSection section) {
                return deserialize.apply(section);
            }

            @Override
            public ConfigSection serialize(T object) {
                return serialize.apply(object);
            }
        };
    }

    static <T> ConfigSerializer<T> of(Function<T, ConfigSection> serialize, Function<ConfigSection, T> deserialize, Function<ConfigSection, Boolean> canDeserialize) {
        return new ConfigSerializer<T>() {
            @Override
            public T deserialize(ConfigSection section) {
                return deserialize.apply(section);
            }

            @Override
            public ConfigSection serialize(T object) {
                return serialize.apply(object);
            }

            @Override
            public boolean canDeserialize(ConfigSection sec) {
                return canDeserialize.apply(sec);
            }
        };
    }

    static <T, R> ConfigSerializer<R> create(Entry<T, R> ent, Functions.Function1<T, R> func) {

        return of(
                obj -> new ConfigSection().with(ent.getKey(), ent.serialize(obj)),
                sec -> func.create(ent.parse(sec)),
                ent::isValid);
    }

    static <T1, T2, R> ConfigSerializer<R> create(Entry<T1, R> ent, Entry<T2, R> ent2, Functions.Function2<T1, T2, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec));
    }

    static <T1, T2, T3, R> ConfigSerializer<R> create(Entry<T1, R> ent, Entry<T2, R> ent2,  Entry<T3, R> ent3, Functions.Function3<T1, T2, T3, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec));
    }

    static <T1, T2, T3, T4, R> ConfigSerializer<R> create(Entry<T1, R> ent, Entry<T2, R> ent2,  Entry<T3, R> ent3, Entry<T4, R> ent4, Functions.Function4<T1, T2, T3, T4, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Functions.Function5<T1, T2, T3, T4, T5, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Functions.Function6<T1, T2, T3, T4, T5, T6, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, T7, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Functions.Function7<T1, T2, T3, T4, T5, T6, T7, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Entry<T8, R> ent8,
            Functions.Function8<T1, T2, T3, T4, T5, T6, T7, T8, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj))
                        .with(ent8.getKey(), ent8.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec),
                        ent8.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec) &&
                        ent8.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Entry<T8, R> ent8,
            Entry<T9, R> ent9,
            Functions.Function9<T1, T2, T3, T4, T5, T6, T7, T8, T9, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj))
                        .with(ent8.getKey(), ent8.serialize(obj))
                        .with(ent9.getKey(), ent9.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec),
                        ent8.parse(sec),
                        ent9.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec) &&
                        ent8.isValid(sec) &&
                        ent9.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Entry<T8, R> ent8,
            Entry<T9, R> ent9,
            Entry<T10, R> ent10,
            Functions.Function10<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj))
                        .with(ent8.getKey(), ent8.serialize(obj))
                        .with(ent9.getKey(), ent9.serialize(obj))
                        .with(ent10.getKey(), ent10.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec),
                        ent8.parse(sec),
                        ent9.parse(sec),
                        ent10.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec) &&
                        ent8.isValid(sec) &&
                        ent9.isValid(sec) &&
                        ent10.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Entry<T8, R> ent8,
            Entry<T9, R> ent9,
            Entry<T10, R> ent10,
            Entry<T11, R> ent11,
            Functions.Function11<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj))
                        .with(ent8.getKey(), ent8.serialize(obj))
                        .with(ent9.getKey(), ent9.serialize(obj))
                        .with(ent10.getKey(), ent10.serialize(obj))
                        .with(ent11.getKey(), ent11.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec),
                        ent8.parse(sec),
                        ent9.parse(sec),
                        ent10.parse(sec),
                        ent11.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec) &&
                        ent8.isValid(sec) &&
                        ent9.isValid(sec) &&
                        ent10.isValid(sec) &&
                        ent11.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Entry<T8, R> ent8,
            Entry<T9, R> ent9,
            Entry<T10, R> ent10,
            Entry<T11, R> ent11,
            Entry<T12, R> ent12,
            Functions.Function12<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj))
                        .with(ent8.getKey(), ent8.serialize(obj))
                        .with(ent9.getKey(), ent9.serialize(obj))
                        .with(ent10.getKey(), ent10.serialize(obj))
                        .with(ent11.getKey(), ent11.serialize(obj))
                        .with(ent12.getKey(), ent12.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec),
                        ent8.parse(sec),
                        ent9.parse(sec),
                        ent10.parse(sec),
                        ent11.parse(sec),
                        ent12.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec) &&
                        ent8.isValid(sec) &&
                        ent9.isValid(sec) &&
                        ent10.isValid(sec) &&
                        ent11.isValid(sec) &&
                        ent12.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Entry<T8, R> ent8,
            Entry<T9, R> ent9,
            Entry<T10, R> ent10,
            Entry<T11, R> ent11,
            Entry<T12, R> ent12,
            Entry<T13, R> ent13,
            Functions.Function13<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj))
                        .with(ent8.getKey(), ent8.serialize(obj))
                        .with(ent9.getKey(), ent9.serialize(obj))
                        .with(ent10.getKey(), ent10.serialize(obj))
                        .with(ent11.getKey(), ent11.serialize(obj))
                        .with(ent12.getKey(), ent12.serialize(obj))
                        .with(ent13.getKey(), ent13.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec),
                        ent8.parse(sec),
                        ent9.parse(sec),
                        ent10.parse(sec),
                        ent11.parse(sec),
                        ent12.parse(sec),
                        ent13.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec) &&
                        ent8.isValid(sec) &&
                        ent9.isValid(sec) &&
                        ent10.isValid(sec) &&
                        ent11.isValid(sec) &&
                        ent12.isValid(sec) &&
                        ent13.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Entry<T8, R> ent8,
            Entry<T9, R> ent9,
            Entry<T10, R> ent10,
            Entry<T11, R> ent11,
            Entry<T12, R> ent12,
            Entry<T13, R> ent13,
            Entry<T14, R> ent14,
            Functions.Function14<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj))
                        .with(ent8.getKey(), ent8.serialize(obj))
                        .with(ent9.getKey(), ent9.serialize(obj))
                        .with(ent10.getKey(), ent10.serialize(obj))
                        .with(ent11.getKey(), ent11.serialize(obj))
                        .with(ent12.getKey(), ent12.serialize(obj))
                        .with(ent13.getKey(), ent13.serialize(obj))
                        .with(ent14.getKey(), ent14.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec),
                        ent8.parse(sec),
                        ent9.parse(sec),
                        ent10.parse(sec),
                        ent11.parse(sec),
                        ent12.parse(sec),
                        ent13.parse(sec),
                        ent14.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec) &&
                        ent8.isValid(sec) &&
                        ent9.isValid(sec) &&
                        ent10.isValid(sec) &&
                        ent11.isValid(sec) &&
                        ent12.isValid(sec) &&
                        ent13.isValid(sec) &&
                        ent14.isValid(sec));
    }


    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Entry<T8, R> ent8,
            Entry<T9, R> ent9,
            Entry<T10, R> ent10,
            Entry<T11, R> ent11,
            Entry<T12, R> ent12,
            Entry<T13, R> ent13,
            Entry<T14, R> ent14,
            Entry<T15, R> ent15,
            Functions.Function15<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj))
                        .with(ent8.getKey(), ent8.serialize(obj))
                        .with(ent9.getKey(), ent9.serialize(obj))
                        .with(ent10.getKey(), ent10.serialize(obj))
                        .with(ent11.getKey(), ent11.serialize(obj))
                        .with(ent12.getKey(), ent12.serialize(obj))
                        .with(ent13.getKey(), ent13.serialize(obj))
                        .with(ent14.getKey(), ent14.serialize(obj))
                        .with(ent15.getKey(), ent15.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec),
                        ent8.parse(sec),
                        ent9.parse(sec),
                        ent10.parse(sec),
                        ent11.parse(sec),
                        ent12.parse(sec),
                        ent13.parse(sec),
                        ent14.parse(sec),
                        ent15.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec) &&
                        ent8.isValid(sec) &&
                        ent9.isValid(sec) &&
                        ent10.isValid(sec) &&
                        ent11.isValid(sec) &&
                        ent12.isValid(sec) &&
                        ent13.isValid(sec) &&
                        ent14.isValid(sec) &&
                        ent15.isValid(sec));
    }

    static <T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> ConfigSerializer<R> create(
            Entry<T1, R> ent,
            Entry<T2, R> ent2,
            Entry<T3, R> ent3,
            Entry<T4, R> ent4,
            Entry<T5, R> ent5,
            Entry<T6, R> ent6,
            Entry<T7, R> ent7,
            Entry<T8, R> ent8,
            Entry<T9, R> ent9,
            Entry<T10, R> ent10,
            Entry<T11, R> ent11,
            Entry<T12, R> ent12,
            Entry<T13, R> ent13,
            Entry<T14, R> ent14,
            Entry<T15, R> ent15,
            Entry<T16, R> ent16,
            Functions.Function16<T1, T2, T3, T4, T5, T6, T7, T8, T9, T10, T11, T12, T13, T14, T15, T16, R> func) {

        return of(
                obj -> new ConfigSection()
                        .with(ent.getKey(), ent.serialize(obj))
                        .with(ent2.getKey(), ent2.serialize(obj))
                        .with(ent3.getKey(), ent3.serialize(obj))
                        .with(ent4.getKey(), ent4.serialize(obj))
                        .with(ent5.getKey(), ent5.serialize(obj))
                        .with(ent6.getKey(), ent6.serialize(obj))
                        .with(ent7.getKey(), ent7.serialize(obj))
                        .with(ent8.getKey(), ent8.serialize(obj))
                        .with(ent9.getKey(), ent9.serialize(obj))
                        .with(ent10.getKey(), ent10.serialize(obj))
                        .with(ent11.getKey(), ent11.serialize(obj))
                        .with(ent12.getKey(), ent12.serialize(obj))
                        .with(ent13.getKey(), ent13.serialize(obj))
                        .with(ent14.getKey(), ent14.serialize(obj))
                        .with(ent15.getKey(), ent15.serialize(obj))
                        .with(ent16.getKey(), ent16.serialize(obj)),
                sec -> func.create(
                        ent.parse(sec),
                        ent2.parse(sec),
                        ent3.parse(sec),
                        ent4.parse(sec),
                        ent5.parse(sec),
                        ent6.parse(sec),
                        ent7.parse(sec),
                        ent8.parse(sec),
                        ent9.parse(sec),
                        ent10.parse(sec),
                        ent11.parse(sec),
                        ent12.parse(sec),
                        ent13.parse(sec),
                        ent14.parse(sec),
                        ent15.parse(sec),
                        ent16.parse(sec)),
                sec -> ent.isValid(sec) &&
                        ent2.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent3.isValid(sec) &&
                        ent4.isValid(sec) &&
                        ent5.isValid(sec) &&
                        ent6.isValid(sec) &&
                        ent7.isValid(sec) &&
                        ent8.isValid(sec) &&
                        ent9.isValid(sec) &&
                        ent10.isValid(sec) &&
                        ent11.isValid(sec) &&
                        ent12.isValid(sec) &&
                        ent13.isValid(sec) &&
                        ent14.isValid(sec) &&
                        ent15.isValid(sec) &&
                        ent16.isValid(sec));
    }


    interface Entry<T, C> {

        String getKey();
        Entry<T, C> orDefault(T defaultValue);
        Entry<T, C> optional();
        T getOrDefault(C value);
        T parse(ConfigSection section);
        boolean isValid(ConfigSection sec);
        Object serialize(C value);
    }

    class SingleEntry<T, C> implements Entry<T, C> {

        private final String key;

        private final Serializer<T, Object> serializer;
        private final Function<C, T> getter;

        private T defaultValue;
        private boolean isOptional;

        public SingleEntry(String key, Serializer<T, Object> serializer, Function<C, T> getter) {
            this.key = key;
            this.serializer = serializer;
            this.getter = getter;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public SingleEntry<T, C> orDefault(T defaultValue) {

            this.defaultValue = defaultValue;
            this.isOptional = true;
            return this;
        }

        @Override
        public SingleEntry<T, C> optional() {

            this.isOptional = true;
            return this;
        }

        @Override
        public T getOrDefault(C value) {

            T val = getter.apply(value);
            return val == null ? defaultValue : val;
        }

        @Override
        public T parse(ConfigSection section) {

            Object o = section.get(key);
            if(o == null && isOptional) {
                return defaultValue;
            }
            return serializer.deserialize(o);
        }

        @Override
        public boolean isValid(ConfigSection sec) {

            if(isOptional) return true;

            Object o = sec.get(key);
            return serializer.canDeserialize(o);
        }

        @Override
        public Object serialize(C value) {

            return serializer.serialize(getOrDefault(value));
        }
    }

    class ListEntry<T, C> implements Entry<Collection<T>, C> {

        private final String key;

        private final Serializer<T, Object> serializer;
        private final Function<C, Collection<T>> getter;
        private Collection<T> defaultValue = new ArrayList<>();

        private boolean isOptional;

        private ListEntry(String key, Serializer<T, Object> serializer, Function<C, Collection<T>> getter) {
            this.key = key;
            this.serializer = serializer;
            this.getter = getter;
        }

        @Override
        public String getKey() {
            return key;
        }

        @Override
        public ListEntry<T, C> orDefault(Collection<T> defaultValue) {
            this.defaultValue = defaultValue;
            this.isOptional = true;
            return this;
        }

        @Override
        public ListEntry<T, C> optional() {
            this.isOptional = true;
            return this;
        }

        @Override
        public Collection<T> getOrDefault(C value) {

            Collection<T> val = getter.apply(value);
            return val == null ? defaultValue : val;
        }

        @Override
        public Collection<T> parse(ConfigSection section) {

            if(isOptional && !section.has(key, List.class)) return defaultValue;
            List<T> out = new ArrayList<>();
            for (Object o : section.getList(key)) {
                out.add(serializer.deserialize(o));
            }
            return out;
        }

        @Override
        public boolean isValid(ConfigSection sec) {

            return isOptional || sec.has(key, List.class);
        }

        @Override
        public Object serialize(C value) {

            Collection<T> val = getOrDefault(value);
            if(val == null) return null;

            List<Object> out = new ArrayList<>();
            for(T t : val) {
                out.add(serializer.serialize(t));
            }

            return out;
        }
    }

    class MapEntry<K, V, C> implements Entry<Map<K, V>, C> {
        private final String key;

        private final InlineSerializer<K> keySerializer;
        private final Serializer<V, Object> valueSerializer;
        private final Function<C, Map<K, V>> getter;
        private Map<K, V> defaultValue = new HashMap<>();


        private boolean isOptional;

        private MapEntry(String key, InlineSerializer<K> keySerializer, Serializer<V, Object> valueSerializer, Function<C, Map<K, V>> getter) {
            this.key = key;
            this.keySerializer = keySerializer;
            this.valueSerializer = valueSerializer;
            this.getter = getter;
        }

        @Override
        public String getKey() {
            return key;
        }

        public MapEntry<K, V, C> orDefault(Map<K, V> defaultValue) {
            this.defaultValue = defaultValue;
            this.isOptional = true;
            return this;
        }

        @Override
        public MapEntry<K, V, C> optional() {
            this.isOptional = true;
            return this;
        }

        @Override
        public Map<K, V> getOrDefault(C value) {

            Map<K, V> val = getter.apply(value);
            if(val == null) return defaultValue;

            return val;
        }

        @Override
        public Map<K, V> parse(ConfigSection section) {

            if(isOptional && !section.has(key, ConfigSection.class)) return defaultValue;

            ConfigSection sec = section.getSection(key);
            HashMap<K, V> out = new HashMap<>();
            for(String s : sec.getKeys()) {
                out.put(keySerializer.deserialize(s), valueSerializer.deserialize(sec.get(s)));
            }

            return out;
        }

        @Override
        public boolean isValid(ConfigSection sec) {

            return isOptional || sec.has(key, ConfigSection.class);
        }

        @Override
        public Object serialize(C value) {

            Map<K, V> val = getOrDefault(value);
            if(val == null) return null;

            Map<String, Object> out = new HashMap<>();
            for(Map.Entry<K, V> ent : val.entrySet()) {
                out.put(keySerializer.serialize(ent.getKey()), valueSerializer.serialize(ent.getValue()));
            }

            return out;
        }
    }
}

