import org.TerraFutura.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import org.json.JSONObject;
import org.json.JSONArray;

public class ActivationPatternUnitTest {

    @Test
    public void testConstructorCopiesPattern() {
        List<SimpleEntry<Integer, Integer>> src = new ArrayList<>();
        src.add(new SimpleEntry<>(1, 2));
        src.add(new SimpleEntry<>(3, 4));

        ActivationPattern pattern = new ActivationPattern(src);

        //modify original list, must not affect internal pattern
        src.add(new SimpleEntry<>(5, 6));

        assertEquals(2, pattern.getPattern().size());
        assertEquals(Integer.valueOf(1), pattern.getPattern().get(0).getKey());
        assertEquals(Integer.valueOf(2), pattern.getPattern().get(0).getValue());
    }

    @Test
    public void testGetPatternReturnsStoredPattern() {
        List<SimpleEntry<Integer, Integer>> src = Arrays.asList(
                new SimpleEntry<>(0, 0),
                new SimpleEntry<>(1, -1)
        );

        ActivationPattern pattern = new ActivationPattern(src);

        ArrayList<SimpleEntry<Integer, Integer>> stored = pattern.getPattern();

        assertEquals(2, stored.size());
        assertEquals(Integer.valueOf(0), stored.get(0).getKey());
        assertEquals(Integer.valueOf(0), stored.get(0).getValue());
        assertEquals(Integer.valueOf(1), stored.get(1).getKey());
        assertEquals(Integer.valueOf(-1), stored.get(1).getValue());
    }

    @Test
    public void testStateContainsCorrectJSONStructure() {
        List<SimpleEntry<Integer, Integer>> data = Arrays.asList(
                new SimpleEntry<>(5, 10),
                new SimpleEntry<>(-2, 3)
        );

        ActivationPattern pattern = new ActivationPattern(data);

        String jsonString = pattern.state();
        JSONObject root = new JSONObject(jsonString);

        assertFalse(root.getBoolean("selected"));

        JSONArray arr = root.getJSONArray("activations");
        assertEquals(2, arr.length());

        JSONObject first = arr.getJSONObject(0);
        assertEquals(5, first.getInt("x"));
        assertEquals(10, first.getInt("y"));

        JSONObject second = arr.getJSONObject(1);
        assertEquals(-2, second.getInt("x"));
        assertEquals(3, second.getInt("y"));
    }


    @Test
    public void testEmptyPattern() {
        ActivationPattern pattern = new ActivationPattern(Collections.emptyList());

        String jsonString = pattern.state();
        JSONObject root = new JSONObject(jsonString);

        assertTrue(root.has("activations"));
        assertEquals(0, root.getJSONArray("activations").length());
        assertFalse(root.getBoolean("selected"));
    }
}
