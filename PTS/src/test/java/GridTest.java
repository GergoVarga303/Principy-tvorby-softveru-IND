import org.TerraFutura.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class GridTest {
    private Grid grid;
    private Card card1;
    private Card card2;

    @Before
    public void setup() {
        grid = new Grid();
        card1 = new Card(null, null, 1);
        card2 = new Card(null, null, 1);
    }

    @Test
    public void testPutAndGetCard(){
        GridPosition pos = GridPosition.START_P33;
        grid.putCard(pos,card1);

        Optional<Card> res = grid.getCard(pos);
        assertTrue(res.isPresent());
        assertEquals(card1, res.get());
    }

    @Test
    public void testCanPutCard(){
        GridPosition center = GridPosition.START_P33; //(0,0)
        grid.putCard(center, card1);

        GridPosition neighbour = GridPosition.P23; //(0,-1)
        assertTrue(grid.canPutCard(neighbour));

        GridPosition far = GridPosition.P22; //(-1,-1) so not direct neighbour
        assertFalse(grid.canPutCard(far));

        assertFalse(grid.canPutCard(center)); //occupied
    }

    @Test
    public void testPutCardUpdatesNeighboursAndLimits(){
        GridPosition center = GridPosition.START_P33; //(0,0)
        grid.putCard(center, card1);

        GridPosition neighbour = GridPosition.P23; //(0,-1)
        grid.putCard(neighbour, card2);

        //neighbouringNotActivated should contain both
        assertTrue(grid.canBeActivated(center));
        assertTrue(grid.canBeActivated(neighbour));

        GridPosition far = GridPosition.P53; //(0,2) should not be able to place it
        assertFalse(grid.canPutCard(far));

        grid.putCard(GridPosition.P43, new Card(null,null,1)); //(0,1)
        assertFalse(grid.canPutCard(far)); //still cant place it, row has too many cards

        grid.putCard(GridPosition.P34,new Card(null,null,1)); //(1,0)
        grid.putCard(GridPosition.P44, new Card(null, null, 1)); //(1,1)

        //still cant place on (1,2), because it doesn't respect the 3x3 format
        assertFalse(grid.canPutCard(GridPosition.P54));
    }

    @Test
    public void testActivationLogic(){
        GridPosition pos = GridPosition.START_P33;
        grid.putCard(pos,card1);

        assertTrue(grid.canBeActivated(pos));
        grid.setActivated(pos);     //after activation, it cannot be activated again
        assertFalse(grid.canBeActivated(pos));

        //in one turn we can only take one card and put it on our grid
        grid.endTurn();


        //next turn, new card
        GridPosition neighbour = GridPosition.P23;
        grid.putCard(neighbour,card2);

        //card 1 is neighbor of card2, should be able to activate
        assertTrue(grid.canBeActivated(pos));
        assertTrue(grid.canBeActivated(neighbour));

        grid.setActivated(pos);
        assertFalse(grid.canBeActivated(pos));
        assertTrue(grid.canBeActivated(neighbour));

        grid.endTurn();

        //activationPattern in the Endgame, no cards taken just a pattern
        AbstractMap.SimpleEntry<Integer, Integer> patternEntry1 = new AbstractMap.SimpleEntry<>(0,0);
        AbstractMap.SimpleEntry<Integer, Integer> patternEntry2 = new AbstractMap.SimpleEntry<>(0,0);
        List<AbstractMap.SimpleEntry<Integer,Integer>> pattern = List.of(patternEntry1,patternEntry2);
        grid.setActivationPattern(pattern);

        assertTrue(grid.canBeActivated(pos));
        assertFalse(grid.canBeActivated(neighbour));
        grid.setActivated(pos);
        assertTrue(grid.canBeActivated(pos));
        grid.setActivated(pos);
        assertFalse(grid.canBeActivated(pos));
    }

    @Test
    public void testStateOutput(){
        GridPosition pos = GridPosition.START_P33;
        grid.putCard(pos, card1);

        String state = grid.state();
        assertTrue(state.contains("Grid:"));
        assertTrue(state.contains(pos.toString()));
        assertTrue(state.contains(card1.state()));
    }
}
