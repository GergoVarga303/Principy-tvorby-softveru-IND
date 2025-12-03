import org.TerraFutura.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class CardUnitTest {

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
    public void testCardInitialState() {
        Card card = new Card(null, null, 2);

        assertEquals(0, card.getResourcesSnapshot().size());
        assertEquals(2, card.getPollutionSpaces());
        assertEquals(0, card.getPollutionOnCard());
        assertFalse(card.isBlockedByPollution());
    }

    /*

    Resources

     */
    @Test
    public void testCanGetResourcesWhenCardHasThem() {
        Card card = new Card(null, null, 2);
        card.putResources(new ArrayList<>(List.of(Resource.Green, Resource.Green, Resource.Red)));

        assertTrue(card.canGetResources(List.of(Resource.Green, Resource.Green, Resource.Red)));
    }

    @Test
    public void testCannotGetMissingResources() {
        Card card = new Card(null, null, 2);
        card.putResources(new ArrayList<>(List.of(Resource.Green, Resource.Green, Resource.Red)));

        assertFalse(card.canGetResources(new ArrayList<>(List.of(Resource.Yellow))));
        assertFalse(card.canGetResources(new ArrayList<>(List.of(Resource.Red,Resource.Red))));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetResources(){
        Card card = new Card(null, null, 2);
        card.putResources(new ArrayList<>(List.of(Resource.Green, Resource.Green, Resource.Red)));
        card.getResources(new ArrayList<>(List.of(Resource.Green)));

        assertEquals(new ArrayList<>(List.of(Resource.Green, Resource.Red)), card.getResourcesSnapshot());
        card.getResources(new ArrayList<>(List.of(Resource.Yellow)));
    }

    /*

    Pollution

     */

    @Test
    public void testCanPutResourcesWhenPollutionFits() {
        Card card = new Card(null, null, 2);

        assertTrue(card.canPutResources(List.of(Resource.Pollution, Resource.Pollution)));
    }

    @Test
    public void testBlocksCardCorrectly() {
        Card card = new Card(null, null, 1);

        card.putResources(List.of(Resource.Pollution,Resource.Green));
        assertFalse(card.isBlockedByPollution());

        card.putResources(List.of(Resource.Pollution));
        assertTrue(card.isBlockedByPollution());
        assertFalse(card.canPutResources(List.of(Resource.Yellow)));
        assertFalse(card.canGetResources(List.of(Resource.Green)));
    }

    /*

    Effect mock

     */
    @Test
    public void testUpperEffectCalled() {
        Card card = new Card(new AlwaysTrueEffect(), null, 1);

        assertTrue(card.check(null, null, 0));
    }

    @Test
    public void testUpperEffectRejects() {
        Card card = new Card(new AlwaysFalseEffect(), null, 1);

        assertFalse(card.check(null, null, 0));
    }

    @Test
    public void testLowerEffectCalled() {
        Card card = new Card(null, new AlwaysTrueEffect(), 1);

        assertTrue(card.checkLower(null, null, 0));
    }


    /*

    State String

     */
    @Test
    public void testStateStringContainsKeyInfo() {
        Card card = new Card(new AlwaysTrueEffect(), null, 2);
        card.putResources(Arrays.asList(Resource.Green, Resource.Pollution));

        String s = card.state();

        assertTrue(s.contains("Green"));
        assertTrue(s.contains("pollutionSpaces=2"));
        assertTrue(s.contains("usedPollutionSpaces=1"));
        assertTrue(s.contains("Upper effect=AlwaysTrueEffect"));
        assertFalse(card.isBlockedByPollution());
    }
}