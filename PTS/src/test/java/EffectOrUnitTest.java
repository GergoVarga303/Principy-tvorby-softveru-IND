import org.TerraFutura.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;


public class EffectOrUnitTest {
    //mock classes used for testing
    private static class AlwaysTrueEffect implements Effect {
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return true;
        }

        public String state() {
            return "AlwaysTrueEffect";
        }
    }

    private static class AlwaysFalseEffect implements Effect {
        public boolean check(List<Resource> input, List<Resource> output, int pollution) {
            return false;
        }

        public String state() {
            return "AlwaysFalseEffect";
        }
    }

    @Test
    public void testCheckReturnsTrueIfAnyChildTrue() {
        EffectOr composite = new EffectOr();
        composite.addEffect(new AlwaysFalseEffect());
        composite.addEffect(new AlwaysTrueEffect());
        composite.addEffect(new AlwaysFalseEffect());

        assertTrue(composite.check(null, null, 0));
    }

    @Test
    public void testCheckReturnsFalseIfAllChildrenFalse() {
        EffectOr composite = new EffectOr();
        composite.addEffect(new AlwaysFalseEffect());
        composite.addEffect(new AlwaysFalseEffect());

        assertFalse(composite.check(null, null, 0));
    }

    @Test
    public void testCheckEmptyCompositeReturnsFalse() {
        EffectOr composite = new EffectOr();

        assertFalse(composite.check(null, null, 0));
    }

    /*

    State String

     */
    @Test
    public void testStateConnectsChildStates() {
        EffectOr composite = new EffectOr();
        composite.addEffect(new AlwaysTrueEffect());
        composite.addEffect(new AlwaysFalseEffect());

        String state = composite.state();

        // Check that both child states appear
        assertTrue(state.contains("AlwaysTrueEffect"));
        assertTrue(state.contains("AlwaysFalseEffect"));

        // Check that "OR" is present
        assertTrue(state.contains("OR"));
    }

}
