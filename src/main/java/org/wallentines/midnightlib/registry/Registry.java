package org.wallentines.midnightlib.registry;

import org.wallentines.mdcfg.serializer.InlineSerializer;

import java.util.Optional;

/**
 * A registry for storing values associated with Identifier keys
 * @param <T> The type of data stored in the registry
 */
public class Registry<T> extends RegistryBase<Identifier, T> {

    protected final String defaultNamespace;

    /**
     * Constructs a new Registry with the given default namespace which does not allow duplicate IDs
     * @param defaultNamespace The default namespace which will be used when deserializing IDs
     */
    public Registry(String defaultNamespace) {
        this(defaultNamespace, false, false, false);
    }

    /**
     * Constructs a new Registry with the given default namespace and duplicate strategy
     * @param defaultNamespace The default namespace which will be used when deserializing IDs
     * @param allowDuplicateValues Whether the same object can be registered to multiple IDs. Defaults to false
     * @param allowNullValues Whether null values can be registered. Defaults to false
     * @param allowEqualValues Whether two equal objects can be registered. Defaults to false
     */
    public Registry(String defaultNamespace, boolean allowDuplicateValues, boolean allowNullValues, boolean allowEqualValues) {
        super(allowDuplicateValues, allowNullValues, allowEqualValues);
        this.defaultNamespace = defaultNamespace;
    }

    /**
     * Gets the default namespace of the registry
     * @return The registry's default namespace
     */
    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    @Override
    public InlineSerializer<T> nameSerializer() {
        return nameSerializer(Identifier.serializer(defaultNamespace));
    }

    public T register(String id, T value) {
        return register(new Identifier(defaultNamespace, id), value);
    }

    /**
     * Creates an inline name serializer using the given Identifier serializer
     * @param idSerializer The identifier serializer to use in place of the default
     * @return A new name serializer
     */
    public InlineSerializer<T> nameSerializer(InlineSerializer<Identifier> idSerializer) {
        return InlineSerializer.of(val -> Optional.ofNullable(getId(val)).map(Object::toString).orElse(null), id -> get(idSerializer.readString(id)));
    }

}
