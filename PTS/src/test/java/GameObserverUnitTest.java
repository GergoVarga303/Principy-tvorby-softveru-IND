import org.TerraFutura.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;


public class GameObserverUnitTest {
    private GameObserver gameObserver;

    private static class MockObserver implements TerraFuturaObserverInterface {
        String lastMessage = null;

        @Override
        public void notify(String GameState) {
            this.lastMessage = GameState;
        }
    }

    private MockObserver obs1;
    private MockObserver obs2;
    private MockObserver obs3;

    @Before
    public void setup() {
        gameObserver = new GameObserver();
        obs1 = new MockObserver();
        obs2 = new MockObserver();
        obs3 = new MockObserver();
    }

    @Test
    public void testAddObservers() {
        gameObserver.addObserver(1, obs1);
        gameObserver.addObserver(2, obs2);

        Map<Integer, String> state = new HashMap<>();
        state.put(1, "A");
        state.put(2, "B");

        gameObserver.notifyAll(state);

        assertEquals("A", obs1.lastMessage);
        assertEquals("B", obs2.lastMessage);
    }
    @Test
    public void testNotifyOnlyExistingObservers() {
        gameObserver.addObserver(1, obs1);
        gameObserver.addObserver(2, obs2);

        Map<Integer, String> state = new HashMap<>();
        state.put(1, "Hi1");
        //missing pid2

        gameObserver.notifyAll(state);

        assertEquals("Hi1", obs1.lastMessage);
        assertNull("Observer 2 should not be notified", obs2.lastMessage);
    }

    @Test
    public void testNotifyOnlyTheirState() {
        gameObserver.addObserver(1, obs1);
        gameObserver.addObserver(2, obs2);
        gameObserver.addObserver(3, obs3);

        Map<Integer, String> state = new HashMap<>();
        state.put(1, "A");
        state.put(2, "B");
        state.put(3, "C");

        gameObserver.notifyAll(state);

        assertEquals("A", obs1.lastMessage);
        assertEquals("B", obs2.lastMessage);
        assertEquals("C", obs3.lastMessage);
    }

    @Test
    public void testNullObserverDoesNotCrash() {
        //null observer
        gameObserver.addObserver(1, null);

        Map<Integer, String> state = new HashMap<>();
        state.put(1, "Hello");

        //should not throw
        gameObserver.notifyAll(state);
    }

    @Test
    public void testNotifyWithEmptyStateMap() {
        gameObserver.addObserver(1, obs1);

        gameObserver.notifyAll(new HashMap<>());

        //no state sent
        assertNull(obs1.lastMessage);
    }

    @Test
    public void testMultipleNotificationsUpdateObservers() {
        gameObserver.addObserver(1, obs1);

        Map<Integer, String> state1 = new HashMap<>();
        state1.put(1, "first");
        gameObserver.notifyAll(state1);

        assertEquals("first", obs1.lastMessage);

        Map<Integer, String> state2 = new HashMap<>();
        state2.put(1, "second");
        gameObserver.notifyAll(state2);

        assertEquals("second", obs1.lastMessage);
    }
}

