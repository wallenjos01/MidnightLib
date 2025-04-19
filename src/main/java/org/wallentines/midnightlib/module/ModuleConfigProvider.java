package org.wallentines.midnightlib.module;

import org.jetbrains.annotations.Nullable;
import org.wallentines.mdcfg.ConfigObject;
import org.wallentines.mdcfg.ConfigSection;
import org.wallentines.mdcfg.registry.Identifier;

public interface ModuleConfigProvider {

    @Nullable
    ConfigSection getConfig(Identifier moduleId);

    void setConfig(Identifier moduleId, ConfigSection config);

    class Default implements ModuleConfigProvider {

        private final ConfigSection section;

        public Default(ConfigSection section) {
            this.section = section;
        }

        public ConfigSection getConfig() {
            return section;
        }

        @Nullable
        @Override
        public ConfigSection getConfig(Identifier moduleId) {
            ConfigObject obj = section.get(moduleId.toString());
            if(obj == null || !obj.isSection()) return null;
            return obj.asSection();
        }

        @Override
        public void setConfig(Identifier moduleId, ConfigSection config) {
            section.set(moduleId.toString(), config);
        }
    }

}
