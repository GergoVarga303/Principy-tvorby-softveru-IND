import org.TerraFutura.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class MoveCardTest {
    private MoveCard mover;
    private Grid grid;

    private Card c1, c2, c3, c4, c5;
    private Pile pile;

    @Before
    public void setup() {
        mover = new MoveCard();
        grid = new Grid();

        //create simple cards
        c1 = new Card(null, null, 1); //hidden
        c2 = new Card(null, null, 1);
        c3 = new Card(null, null, 1);
        c4 = new Card(null, null, 1);
        c5 = new Card(null, null, 1);

        pile = new Pile(List.of(c1, c2, c3, c4, c5));
    }

    @Test
    public void testMoveCardNullArguments() {
        assertFalse(mover.moveCard(null, GridPosition.START_P33, grid, 0));
        assertFalse(mover.moveCard(pile, null, grid, 0));
        assertFalse(mover.moveCard(pile, GridPosition.START_P33, null, 0));
    }

    @Test
    public void testMoveCardInvalidIndex() {
        assertFalse(mover.moveCard(pile, GridPosition.START_P33, grid, 10));
        assertFalse(mover.moveCard(pile, GridPosition.START_P33, grid, -5));
    }

    @Test
    public void testMoveCardCannotPutOnGrid() {
        //place a card on (0,0)
        grid.putCard(GridPosition.START_P33, c1); //(0,0)

        //move to not neighbour
        assertFalse(mover.moveCard(pile, GridPosition.P11, grid, 0));
    }

    /*

    Take visible Card

     */
    @Test
    public void testMoveVisibleCard() {
        // First place a card to allow neighbors:
        grid.putCard(GridPosition.START_P33, new Card(null, null, 1));

        boolean result = mover.moveCard(pile, GridPosition.P34, grid, 0); //(1,0)

        assertTrue(result);

        // verify card moved
        Optional<Card> placed = grid.getCard(GridPosition.P34);
        assertTrue(placed.isPresent());
        assertEquals(c2, placed.get());

        //verify card no longer in visible cards
        assertNotEquals(c2, pile.getCard(0).orElse(null));
    }

    /*

    Take hidden Card

     */
    @Test
    public void testMoveHiddenCard() {
        // Place initial card to allow neighbor
        grid.putCard(GridPosition.START_P33, new Card(null, null, 1));
        // Move hidden top card (c1)
        boolean result = mover.moveCard(pile, GridPosition.P34, grid, -1);
        assertTrue(result);

        // verify card moved
        Optional<Card> placed = grid.getCard(GridPosition.P34);
        assertTrue(placed.isPresent());
        assertEquals(c1, placed.get());

        //verify card no longer on top of hidden pile
        assertNotEquals(c1, pile.getCard(-1).orElse(null));
    }

    /*

    Pile and Grid Integration

     */
    @Test
    public void testIntegrationMoveCardFromPileToGrid() {
        // Put starting card to allow neighboring placement
        grid.putCard(GridPosition.START_P33, new Card(null, null, 1));

        // Move card c2 to (1,0)
        assertTrue(mover.moveCard(pile, GridPosition.P34, grid, 0));

        // Grid should contain the card
        Optional<Card> gridCard = grid.getCard(GridPosition.P34);
        assertTrue(gridCard.isPresent());
        assertEquals(c2, gridCard.get());

        // Pile should have taken 1 visible card
        // and replaced it with top hidden card (c1)
        Card newVisible0 = pile.getCard(0).orElse(null);
        assertNotNull(newVisible0);
        assertEquals(c1, newVisible0);

        // Move first visible card c3
        assertTrue(mover.moveCard(pile, GridPosition.P32, grid, 0));

        Optional<Card> secondGridCard = grid.getCard(GridPosition.P32);
        assertTrue(secondGridCard.isPresent());
        assertEquals(c1, secondGridCard.get());
    }
}
