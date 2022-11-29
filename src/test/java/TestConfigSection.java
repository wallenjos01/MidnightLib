import org.junit.Assert;
import org.junit.Test;
import org.wallentines.midnightlib.config.ConfigSection;
import org.wallentines.midnightlib.config.serialization.json.JsonConfigProvider;

import java.util.ArrayList;
import java.util.List;

public class TestConfigSection {

    @Test
    public void testKeyOrdering() {

        ConfigSection sec = new ConfigSection();
        sec.set("Hello", "World");
        sec.set("int", 1124);
        sec.set("float", 0.45);
        sec.set("bool", true);

        int i = 0;
        for(String s : sec.getKeys()) {

            if(i == 0) Assert.assertEquals("Hello", s);
            if(i == 1) Assert.assertEquals("int", s);
            if(i == 2) Assert.assertEquals("float", s);
            if(i == 3) Assert.assertEquals("bool", s);

            i++;
        }

        String json = JsonConfigProvider.INSTANCE.saveToString(sec);
        ConfigSection parsed = JsonConfigProvider.INSTANCE.loadFromString(json);

        i = 0;
        for(String s : parsed.getKeys()) {

            if(i == 0) Assert.assertEquals("Hello", s);
            if(i == 1) Assert.assertEquals("int", s);
            if(i == 2) Assert.assertEquals("float", s);
            if(i == 3) Assert.assertEquals("bool", s);

            i++;
        }
    }

    @Test
    public void testListOrdering() {

        List<String> list = new ArrayList<>();
        list.add("item1");
        list.add("item2");
        list.add("item3");
        list.add("item4");
        list.add("item5");

        ConfigSection section = new ConfigSection().with("list", list);

        Assert.assertEquals("item1" , section.getStringList("list").get(0));
        Assert.assertEquals("item2" , section.getStringList("list").get(1));
        Assert.assertEquals("item3" , section.getStringList("list").get(2));
        Assert.assertEquals("item4" , section.getStringList("list").get(3));
        Assert.assertEquals("item5" , section.getStringList("list").get(4));


        String json = JsonConfigProvider.INSTANCE.saveToString(section);
        ConfigSection parsed = JsonConfigProvider.INSTANCE.loadFromString(json);

        Assert.assertEquals("item1" , parsed.getStringList("list").get(0));
        Assert.assertEquals("item2" , parsed.getStringList("list").get(1));
        Assert.assertEquals("item3" , parsed.getStringList("list").get(2));
        Assert.assertEquals("item4" , parsed.getStringList("list").get(3));
        Assert.assertEquals("item5" , parsed.getStringList("list").get(4));
    }
}
