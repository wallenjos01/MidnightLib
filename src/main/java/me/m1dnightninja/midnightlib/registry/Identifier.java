package me.m1dnightninja.midnightlib.registry;

import me.m1dnightninja.midnightlib.config.InlineSerializer;

public class Identifier {


    private static final IllegalArgumentException EXCEPTION = new IllegalArgumentException("Unable to parse MIdentifier!");

    private final String namespace;
    private final String path;

    public Identifier(String namespace, String path) {
        this.namespace = namespace;
        this.path = path;
    }

    public String getNamespace() {
        return namespace;
    }

    public String getPath() {
        return path;
    }

    public static Identifier parse(String toParse) throws IllegalArgumentException {

        if(toParse == null) throw EXCEPTION;

        if(!toParse.contains(":")) {
            throw EXCEPTION;
        }

        String[] ss = toParse.split(":");
        if(ss.length > 2) {
            throw EXCEPTION;
        }

        return new Identifier(ss[0], ss[1]);
    }

    public static Identifier parseOrDefault(String toParse) {
        return parseOrDefault(toParse, "minecraft");
    }

    public static Identifier parseOrDefault(String toParse, String defaultNamespace) {

        if(toParse == null) throw EXCEPTION;

        String[] ss = toParse.split(":");

        return ss.length == 1 ? new Identifier(defaultNamespace, toParse) : new Identifier(ss[0], ss[1]);
    }

    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Identifier) {

            Identifier mid = (Identifier) obj;
            return mid.namespace.equals(namespace) && mid.path.equals(path);

        } else {

            try {
                Identifier mid = parse(obj.toString());
                return equals(mid);

            } catch(IllegalArgumentException ex) {
                return false;
            }
        }
    }

    private static boolean isValid(String[] strs) {

        return strs.length > 1 && strs[0] != null && strs[0].length() > 0 && strs[1] != null && strs[1].length() > 0;
    }

    @Override
    public String toString() {
        return namespace + ":" + path;
    }

    @Override
    public int hashCode() {
        return toString().hashCode();
    }

    public static final InlineSerializer<Identifier> SERIALIZER = new InlineSerializer<Identifier>() {
        @Override
        public Identifier deserialize(String s) {
            return parseOrDefault(s);
        }

        @Override
        public String serialize(Identifier object) {
            return object.toString();
        }

        @Override
        public boolean canDeserialize(String s) {
            return s.length() > 0 && (!s.contains(":") || isValid(s.split(":")));
        }
    };

}
