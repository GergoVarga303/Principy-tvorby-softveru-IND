import org.TerraFutura.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;


public class PileTest {
    private Pile pile;
    private Card card1, card2, card3, card4, card5, card6;

    @Before
    public void setup() {
        card1 = new Card(null, null, 1);
        card2 = new Card(null, null, 1);
        card3 = new Card(null, null, 1);
        card4 = new Card(null, null, 1);
        card5 = new Card(null, null, 1);
        card6 = new Card(null, null, 1);

        // Initialize pile with 6 cards
        pile = new Pile(List.of(card1, card2, card3, card4, card5, card6));
    }

    @Test
    public void testInitialization() {
        //last 4 cards should be visible: 3,4,5 and 6
        assertEquals(Optional.of(card3), pile.getCard(0));
        assertEquals(Optional.of(card4), pile.getCard(1));
        assertEquals(Optional.of(card5), pile.getCard(2));
        assertEquals(Optional.of(card6), pile.getCard(3));

        //top of hidden pile
        assertEquals(Optional.of(card2), pile.getCard(-1));
    }

    //invalid indexing
    @Test
    public void testGetCardInvalidIndex() {
        assertEquals(Optional.empty(), pile.getCard(4));
        assertEquals(Optional.empty(), pile.getCard(-2));
    }

    @Test
    public void testTakeVisibleCard() {
        //take card at index 1 (card4)
        pile.takeCard(1);

        assertEquals(Optional.of(card2), pile.getCard(0)); //top of hidden pile became first
        assertEquals(Optional.of(card3), pile.getCard(1)); //first card became second

    }

    @Test
    public void testTakeHiddenCard() {
        pile.takeCard(-1);

        //last hidden card (card2) removed
        assertEquals(Optional.of(card1), pile.getCard(-1));
    }

    @Test
    public void testRemoveLastCard() {
        // Before: visible = card6, card5, card4, card3 ; hidden = card1, card2
        pile.removeLastCard();

        // Last visible card (card3) removed, top hidden (card2) added first
        assertEquals(Optional.of(card2), pile.getCard(0));
        assertEquals(Optional.of(card3), pile.getCard(1));
        assertEquals(Optional.of(card4), pile.getCard(2));
        assertEquals(Optional.of(card5), pile.getCard(3));

        // Hidden now has only card1
        assertEquals(Optional.of(card1), pile.getCard(-1));
    }

    @Test
    public void testStateContainsVisibleAndHidden() {
        String state = pile.state();
        assertTrue(state.contains("Visible cards"));
        assertTrue(state.contains("Hidden cards: 2"));
        assertTrue(state.contains(card3.state())); // first visible
        assertTrue(state.contains(card6.state())); // last visible
    }
}
