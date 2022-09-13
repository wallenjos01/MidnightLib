package org.wallentines.midnightlib.config;

import org.wallentines.midnightlib.config.serialization.ConfigSerializer;
import org.wallentines.midnightlib.config.serialization.InlineSerializer;

import java.util.*;

@SuppressWarnings("unused")
public class ConfigSection {
    private final ConfigRegistry reg;
    private final LinkedHashMap<String, Object> entries = new LinkedHashMap<>();

    public ConfigSection() {
        this(ConfigRegistry.INSTANCE);
    }

    public ConfigSection(ConfigRegistry reg) {
        this.reg = reg;
    }

    public <T> void set(String key, T obj) {
        // Remove an object
        if (obj == null) {

            this.entries.remove(key);

        } else {

            this.entries.put(key, serialize(obj));
        }
    }

    @SuppressWarnings("unchecked")
    private <T> Object serialize(T obj) {

        // Try to Serialize Map
        if (obj instanceof Map) {

            ConfigSection out = new ConfigSection();
            HashMap<String, ?> map = serializeMap((Map<?,?>) obj);

            for (Map.Entry<String, ?> ent : map.entrySet()) {
                out.set(ent.getKey(), ent.getValue());
            }
            return out;
        }

        // Try to serialize List elements
        if(obj instanceof Collection) {
            List<Object> serialized = new ArrayList<>();
            for (Object o : (Collection<?>) obj) {
                serialized.add(serialize(o));
            }
            return serialized;

        }
        // Check if the object is a primitive or section
        if(obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof ConfigSection) return obj;

        if (reg != null) {

            // Try to serialize as a String
            InlineSerializer<T> inlineSerializer = reg.getInlineSerializer((Class<T>) obj.getClass(), ConfigRegistry.Direction.SERIALIZE);
            if(inlineSerializer != null) return inlineSerializer.serialize(obj);

            // Try to serialize as a ConfigSection
            ConfigSerializer<T> serializer = reg.getSerializer((Class<T>) obj.getClass(), ConfigRegistry.Direction.SERIALIZE);
            if (serializer != null) return serializer.serialize(obj);
        }

        // Convert to a string if we cannot serialize
        return obj.toString();
    }

    public void setMap(String id, String keyLabel, String valueLabel, Map<?, ?> map) {

        List<ConfigSection> lst = new ArrayList<>();
        for(Map.Entry<?, ?> ent : map.entrySet()) {
            ConfigSection sec = new ConfigSection();
            sec.set(keyLabel, ent.getKey());
            sec.set(valueLabel, ent.getValue());
            lst.add(sec);
        }

        set(id, lst);
    }

    public ConfigSection with(String key, Object value) {

        set(key, value);
        return this;
    }

    public Object get(String key) {
        return this.entries.get(key);
    }

    public <T> T get(String key, Class<T> clazz) {
        Object out = this.get(key);
        if(out == null) throw new IllegalArgumentException("Unable to convert null to " + clazz.getName() + "! Section has no value with key " + key + "!");

        return convert(out, clazz);
    }

    public Object getOrDefault(String key, Object def) {

        return entries.getOrDefault(key, def);
    }

    public <T> T getOrDefault(String key, T def, Class<T> clazz) {

        Object out = this.get(key);
        if(out != null) {
            try {
                return convert(out, clazz);
            } catch (Exception ex) {
                // Ignore
            }
        }

        return def;
    }

    public Iterable<String> getKeys() {
        return entries.keySet();
    }

    public Map<String, Object> getEntries() {
        return new HashMap<>(entries);
    }

    public boolean has(String key) {
        return this.entries.containsKey(key);
    }

    public <T> boolean has(String key, Class<T> clazz) {
        Object out = this.get(key);
        if(out == null) return false;

        return canConvert(out, clazz);
    }

    public String getString(String key) {
        return this.get(key, String.class);
    }

    public int getInt(String key) {
        return this.get(key, Number.class).intValue();
    }

    public float getFloat(String key) {
        return this.get(key, Number.class).floatValue();
    }

    public double getDouble(String key) {
        return this.get(key, Number.class).doubleValue();
    }

    public long getLong(String key) { return this.get(key, Number.class).longValue(); }

    public boolean getBoolean(String key) {

        return getBoolean(key, false);
    }

    public boolean getBoolean(String key, boolean def) {

        Object o = this.get(key);

        if(o == null) return def;

        if(o instanceof Boolean) {
            return (boolean) o;

        } else if(o instanceof Number) {
            return ((Number) o).intValue() != 0;

        } else if(o instanceof String) {
            return o.equals("true");
        }

        return convert(o, Boolean.class);
    }

    public List<?> getList(String key) {
        return this.get(key, List.class);
    }

    public List<String> getStringList(String key) {
        List<?> orig = this.getList(key);
        ArrayList<String> out = new ArrayList<>();
        for (Object o : orig) {
            out.add(o.toString());
        }
        return out;
    }

    public <T> List<T> getList(String key, Class<T> clazz) {

        List<?> lst = getList(key);
        List<T> out = new ArrayList<>(lst.size());
        for(Object o : lst) {
            out.add(convert(o, clazz));
        }

        return out;
    }

    public <T> List<T> getListFiltered(String key, Class<T> clazz) {

        List<?> lst = getList(key);
        List<T> out = new ArrayList<>();
        for(Object o : lst) {
            if(!canConvert(o, clazz)) continue;
            out.add(convert(o, clazz));
        }

        return out;
    }

    public <T> List<T> getListFiltered(String key, ConfigSerializer<T> serializer) {

        List<?> lst = getList(key);
        List<T> out = new ArrayList<>();
        for(Object o : lst) {
            out.add(serializer.deserialize((ConfigSection) o));
        }
        return out;
    }

    public <T> List<T> getListFiltered(String key, InlineSerializer<T> serializer) {

        List<?> lst = getList(key);
        List<T> out = new ArrayList<>();
        for(Object o : lst) {
            out.add(serializer.deserialize(o.toString()));
        }
        return out;
    }

    public ConfigSection getSection(String key) {
        return this.get(key, ConfigSection.class);
    }

    public ConfigSection getOrCreateSection(String key) {
        if(has(key, ConfigSection.class)) {
            return getSection(key);
        }
        ConfigSection sec = new ConfigSection();
        set(key, sec);
        return sec;
    }

    public void fill(ConfigSection other) {
        for(Map.Entry<String, Object> entry : other.getEntries().entrySet()) {
            if(!has(entry.getKey())) {
                set(entry.getKey(), entry.getValue());
            }
        }
    }

    public void fillOverwrite(ConfigSection other) {
        for(Map.Entry<String, Object> entry : other.getEntries().entrySet()) {
            set(entry.getKey(), entry.getValue());
        }
    }

    public ConfigSection copy() {

        ConfigSection out = new ConfigSection();
        for(Map.Entry<String, Object> ent : entries.entrySet()) {

            Object val = ent.getValue();
            if(val instanceof ConfigSection) {
                out.set(ent.getKey(), ((ConfigSection) val).copy());
            } else if(val instanceof Collection) {
                out.set(ent.getKey(), new ArrayList<>((Collection<?>) val));
            } else {
                out.set(ent.getKey(), ent.getValue());
            }
        }
        return out;
    }

    @SuppressWarnings("unchecked")
    private <T> T convert(Object o, Class<T> clazz) {

        if(o == null) {
            throw new IllegalStateException("Unable to convert null to " + clazz.getName() + "!");
        }

        if (o.getClass() == clazz || clazz.isAssignableFrom(o.getClass())) {
            return (T) o;
        }

        if(reg != null) {

            if(o instanceof ConfigSection) {
                ConfigSerializer<T> serializer = reg.getSerializer(clazz, ConfigRegistry.Direction.DESERIALIZE);
                if (serializer != null) {
                    T ret = serializer.deserialize((ConfigSection) o);
                    if (ret == null) {
                        throw new IllegalStateException("Invalid Type! " + o.getClass().getName() + " cannot be converted to " + clazz.getName());
                    }
                    return ret;
                }
            }

            if(o instanceof String) {
                InlineSerializer<T> inlineSerializer = reg.getInlineSerializer(clazz, ConfigRegistry.Direction.DESERIALIZE);
                if (inlineSerializer != null) {
                    T ret = inlineSerializer.deserialize((String) o);
                    if (ret == null) {
                        throw new IllegalStateException("Invalid Type! " + o.getClass().getName() + " cannot be converted to " + clazz.getName());
                    }
                    return ret;
                }
            }
        }

        throw new IllegalStateException("Invalid Type! " + o.getClass().getName() + " cannot be converted to " + clazz.getName());
    }


    private <T> boolean canConvert(Object o, Class<T> clazz) {

        if(reg != null) {
            if(reg.canDeserialize(clazz) && o instanceof ConfigSection) {
                ConfigSerializer<T> ser = reg.getSerializer(clazz, ConfigRegistry.Direction.DESERIALIZE);
                return ser.canDeserialize((ConfigSection) o);
            }
            if(reg.canDeserializeInline(clazz)) {

                InlineSerializer<T> ser = reg.getInlineSerializer(clazz, ConfigRegistry.Direction.DESERIALIZE);
                return ser.canDeserialize(o.toString());
            }
        }

        return clazz.isAssignableFrom(o.getClass());
    }

    @SuppressWarnings("unchecked")
    private <K, V> HashMap<String, V> serializeMap(Map<K,V> in) {

        HashMap<String, V> out = new HashMap<>();
        if(in.isEmpty()) return out;

        InlineSerializer<K> ser = InlineSerializer.of(Objects::toString, str -> null);
        if(reg != null) {
            InlineSerializer<K> ser1 = reg.getInlineSerializer((Class<K>) in.entrySet().iterator().next().getKey().getClass(), ConfigRegistry.Direction.SERIALIZE);
            if(ser1 != null) ser = ser1;
        }

        for(Map.Entry<K, V> ent : in.entrySet()) {
            out.put(ser.serialize(ent.getKey()), ent.getValue());
        }

        return out;
    }


    @Override
    public String toString() {
        return reg.getDefaultProvider().saveToString(this);
    }

}

