package org.wallentines.midnightlib.registry;

import org.wallentines.mdcfg.serializer.InlineSerializer;

public class Registry<T> extends RegistryBase<Identifier, T> {

    protected final String defaultNamespace;

    public Registry(String defaultNamespace) {
        this.defaultNamespace = defaultNamespace;
    }

    public String getDefaultNamespace() {
        return defaultNamespace;
    }

    @Override
    public InlineSerializer<T> nameSerializer() {
        return nameSerializer(Identifier.serializer(defaultNamespace));
    }

    public InlineSerializer<T> nameSerializer(InlineSerializer<Identifier> idSerializer) {
        return InlineSerializer.of(val -> getId(val).toString(), id -> get(idSerializer.readString(id)));
    }

}
