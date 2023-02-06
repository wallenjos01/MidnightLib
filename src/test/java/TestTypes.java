import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.wallentines.midnightlib.types.SortedCollection;

import java.util.ArrayList;
import java.util.List;

public class TestTypes {

    @Test
    public void testSortedCollection() {

        SortedCollection<Integer> ints = new SortedCollection<>();
        ints.add(10);
        ints.add(50);
        ints.add(0);

        List<Integer> out = new ArrayList<>(ints);
        Assertions.assertEquals(0, out.get(0));
        Assertions.assertEquals(10, out.get(1));
        Assertions.assertEquals(50, out.get(2));

    }

}
