package org.wallentines.midnightlib.config;

import org.wallentines.midnightlib.config.serialization.ConfigSerializer;
import org.wallentines.midnightlib.config.serialization.InlineSerializer;
import org.wallentines.midnightlib.math.*;
import org.wallentines.midnightlib.registry.Identifier;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class ConfigRegistry {

    public static final ConfigRegistry INSTANCE = new ConfigRegistry();

    private final HashMap<Class<?>, ConfigSerializer<?>> serializers = new HashMap<>();
    private final HashMap<Class<?>, InlineSerializer<?>> inlineSerializers = new HashMap<>();

    private final List<ConfigProvider> providers = new ArrayList<>();
    private final HashMap<String, Integer> providersByExtension = new HashMap<>();

    private ConfigProvider defaultProvider;

    public ConfigProvider getDefaultProvider() {
        if(defaultProvider == null) {
            if(providers.size() == 0) {
                throw new IllegalStateException("There are no ConfigProviders registered!");
            }
            return providers.get(0);
        }
        return defaultProvider;
    }

    public void setDefaultProvider(ConfigProvider defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    public <T> void registerSerializer(Class<T> clazz, ConfigSerializer<T> serializer) {
        this.serializers.put(clazz, serializer);
    }

    public <T> void registerInlineSerializer(Class<T> clazz, InlineSerializer<T> serializer) {
        this.inlineSerializers.put(clazz, serializer);
    }

    @SuppressWarnings("unchecked")
    public <T> ConfigSerializer<T> getSerializer(Class<T> clazz, Direction dir) {

        ConfigSerializer<T> out = (ConfigSerializer<T>) serializers.get(clazz);
        if(out != null) return out;

        for(Class<?> ser : serializers.keySet()) {
            if(
                dir == Direction.SERIALIZE && ser.isAssignableFrom(clazz) ||
                dir == Direction.DESERIALIZE && clazz.isAssignableFrom(ser)
            )
                return (ConfigSerializer<T>) serializers.get(ser);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    public <T> InlineSerializer<T> getInlineSerializer(Class<T> clazz, Direction dir) {

        InlineSerializer<T> out = (InlineSerializer<T>) inlineSerializers.get(clazz);
        if(out != null) return out;

        for(Class<?> ser : inlineSerializers.keySet()) {
            if(
                dir == Direction.SERIALIZE && ser.isAssignableFrom(clazz) ||
                dir == Direction.DESERIALIZE && clazz.isAssignableFrom(ser)
            )
                return (InlineSerializer<T>) inlineSerializers.get(ser);
        }

        return null;
    }

    public boolean canSerialize(Class<?> clazz) {
        return getSerializer(clazz, Direction.SERIALIZE) != null;
    }

    public boolean canSerializeInline(Class<?> clazz) {
        return getInlineSerializer(clazz, Direction.SERIALIZE) != null;
    }

    public boolean canDeserialize(Class<?> clazz) {
        return getSerializer(clazz, Direction.DESERIALIZE) != null;
    }

    public boolean canDeserializeInline(Class<?> clazz) {
        return getInlineSerializer(clazz, Direction.DESERIALIZE) != null;
    }

    public <T extends ConfigProvider> T registerProvider(T prov) {
        if(providersByExtension.containsKey(prov.getFileExtension())) return null;

        int index = providers.size();
        providers.add(prov);
        providersByExtension.put(prov.getFileExtension(), index);

        return prov;
    }

    public ConfigProvider getProviderForFileType(String extension) {

        Integer index = providersByExtension.get(extension);
        if(index == null) return null;

        return providers.get(index);
    }

    public ConfigProvider getProviderForFile(File f) {

        String name = f.getName();
        if(name.contains(".")) {
            return getProviderForFileType(name.substring(name.lastIndexOf(".")));

        } else {
            return getDefaultProvider();
        }

    }

    public static final InlineSerializer<UUID> UUID_SERIALIZER = new InlineSerializer<UUID>() {
        @Override
        public UUID deserialize(String string) {
            return UUID.fromString(string);
        }

        @Override
        public String serialize(UUID object) {
            return object.toString();
        }
    };

    public void setupDefaults(String defaultNamespace, ConfigProvider provider) {

        setDefaultProvider(registerProvider(provider));

        registerInlineSerializer(Identifier.class, new Identifier.Serializer(defaultNamespace));
        registerInlineSerializer(Vec3d.class, Vec3d.SERIALIZER);
        registerInlineSerializer(Vec3i.class, Vec3i.SERIALIZER);
        registerInlineSerializer(Vec2d.class, Vec2d.SERIALIZER);
        registerInlineSerializer(Vec2i.class, Vec2i.SERIALIZER);
        registerInlineSerializer(Region.class, Region.SERIALIZER);
        registerInlineSerializer(Color.class, Color.SERIALIZER);
        registerInlineSerializer(UUID.class, UUID_SERIALIZER);
    }

    public enum Direction {

        SERIALIZE,
        DESERIALIZE

    }

}

