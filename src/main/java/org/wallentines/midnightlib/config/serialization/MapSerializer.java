package org.wallentines.midnightlib.config.serialization;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class MapSerializer<K,V,O> implements Serializer<Map<K,V>, Map<String, O>> {

    private final Serializer<K, String> keySerializer;
    private final Serializer<V, O> valueSerializer;

    public MapSerializer(Serializer<K, String> keySerializer, Serializer<V, O> valueSerializer) {
        this.keySerializer = keySerializer;
        this.valueSerializer = valueSerializer;
    }

    @Override
    public Map<String, O> serialize(Map<K, V> value) {
        Map<String, O> out = new HashMap<>();
        for(Map.Entry<K, V> ent : value.entrySet()) {
            String key = keySerializer.serialize(ent.getKey());
            O val = valueSerializer.serialize(ent.getValue());
            out.put(key, val);
        }
        return out;
    }

    @Override
    public Map<K, V> deserialize(Map<String, O> object) {
        Map<K, V> out = new HashMap<>();
        for(Map.Entry<String, O> ent : object.entrySet()) {
            K key = keySerializer.deserialize(ent.getKey());
            V val = valueSerializer.deserialize(ent.getValue());
            out.put(key, val);
        }
        return out;
    }

    @Override
    public boolean canDeserialize(Map<String, O> object) {
        for(Map.Entry<String, O> ent : object.entrySet()) {
            if(!keySerializer.canDeserialize(ent.getKey()) || !valueSerializer.canDeserialize(ent.getValue())) return false;
        }
        return true;
    }

    @Override
    public <R> ConfigSerializer.Entry<Map<K, V>, R> entry(String key, Function<R, Map<K, V>> getter) {
        return ConfigSerializer.entry(this, key, getter);
    }


/*    @Override
    public ConfigSection serialize(Map<K, V> value) {
        ConfigSection out = new ConfigSection();
        for(Map.Entry<K, V> ent : value.entrySet()) {
            String key = keySerializer.serialize(ent.getKey());
            O val = valueSerializer.serialize(ent.getValue());
            out.set(key, val);
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map<K, V> deserialize(ConfigSection object) {
        Map<K, V> out = new HashMap<>();
        for(String s : object.getKeys()) {
            K key = keySerializer.deserialize(s);
            V val = valueSerializer.deserialize((O) object.get(s));
            out.put(key, val);
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean canDeserialize(ConfigSection object) {
        for(Map.Entry<String, Object> ent : object.getEntries().entrySet()) {
            if(!keySerializer.canDeserialize(ent.getKey()) || !valueSerializer.canDeserialize((O) ent.getValue())) return false;
        }
        return true;
    }*/
}
