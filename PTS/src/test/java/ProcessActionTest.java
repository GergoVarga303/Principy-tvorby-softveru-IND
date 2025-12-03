import org.TerraFutura.*;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;


public class ProcessActionTest {
    private Grid grid;
    private Card cardBasic;
    private Card cardProduct;
    private ProcessAction processor;
    private GridPosition pos1;
    private GridPosition pos2;

    @Before
    public void setup() {
        processor = new ProcessAction();
        grid = new Grid();

        pos1 = GridPosition.START_P33;
        pos2 = GridPosition.P32;

        cardBasic = new Card(
                new ArbitraryBasic(1, List.of(Resource.Green), 0),
                null,
                1
        );
        grid.putCard(pos1, cardBasic);

        cardProduct = new Card(
                new ArbitraryProduct(),
                null,
                1
        );
        grid.putCard(pos2, cardProduct);

        cardBasic.putResources(List.of(Resource.Red));
        cardProduct.putResources(List.of(Resource.Car));
    }

    /*

    Null

     */
    @Test
    public void testNullCardOrGridReturnsFalse() {
        assertFalse(processor.activateCard(null, grid, null, null, null));
        assertFalse(processor.activateCard(cardBasic, null, null, null, null));
    }

    @Test
    public void testEmptyInputsOutputsPollutionWorks() {
        assertFalse(processor.activateCard(cardBasic, grid, null, null, null));
    }

    /*

    Activations

     */
    @Test
    public void testActivateCardArbitraryBasic() {
        List<Pair<Resource, GridPosition>> inputs = List.of(new Pair<>(Resource.Red, pos1));
        List<Pair<Resource, GridPosition>> outputs = List.of(new Pair<>(Resource.Green, pos1));
        List<GridPosition> pollution = List.of();

        boolean result = processor.activateCard(cardBasic, grid, inputs, outputs, pollution);
        assertTrue(result);

        //input should be consumed
        assertFalse(cardBasic.getResourcesSnapshot().contains(Resource.Red));
        //output should be added to card
        assertTrue(cardBasic.getResourcesSnapshot().contains(Resource.Green));
    }

    @Test
    public void testActivateCardArbitraryProduct() {
        List<Pair<Resource, GridPosition>> inputs = List.of(new Pair<>(Resource.Car, pos2));
        List<Pair<Resource, GridPosition>> outputs = List.of(new Pair<>(Resource.Bulb, pos2));
        List<GridPosition> pollution = List.of();

        boolean result = processor.activateCard(cardProduct, grid, inputs, outputs, pollution);
        assertTrue(result);
        // Input resource consumed
        assertFalse(cardProduct.getResourcesSnapshot().contains(Resource.Car));
        // Output resource added
        assertTrue(cardProduct.getResourcesSnapshot().contains(Resource.Bulb));
    }

    @Test
    public void testActivateCardNoResourceOnSelectedCard(){
        List<Pair<Resource, GridPosition>> inputs = List.of(new Pair<>(Resource.Red, pos2)); //Red not found on pos2
        List<Pair<Resource, GridPosition>> outputs = List.of(new Pair<>(Resource.Green, pos1));
        List<GridPosition> pollution = List.of();

        boolean result = processor.activateCard(cardBasic, grid, inputs, outputs, pollution);
        assertFalse(result);
    }

    @Test
    public void testActivateCardInvalidInput(){
        List<Pair<Resource, GridPosition>> inputs = List.of(new Pair<>(Resource.Yellow, pos1));
        List<Pair<Resource, GridPosition>> outputs = List.of(new Pair<>(Resource.Green, pos1));

        assertFalse(processor.activateCard(cardBasic, grid, inputs, outputs, null));
    }

    @Test
    public void testActivateCardOutputsToWrongCard(){
        List<Pair<Resource, GridPosition>> inputs = List.of(new Pair<>(Resource.Red, pos1));
        List<Pair<Resource, GridPosition>> outputs = List.of(new Pair<>(Resource.Green, pos2));

        assertFalse(processor.activateCard(cardBasic, grid, inputs, outputs, null));
    }

    /*

    Pollution

     */
    @Test
    public void testActivateCardWithPollution() {
        GridPosition pos3 = GridPosition.P12;
        Card cardBasicPollutionEffect = new Card(null,new ArbitraryBasic(1,List.of(Resource.Green),1),1);
        cardBasicPollutionEffect.putResources(List.of(Resource.Red));
        grid.putCard(pos3,cardBasicPollutionEffect);

        List<Pair<Resource, GridPosition>> inputs = List.of(new Pair<>(Resource.Red, pos3));
        List<Pair<Resource, GridPosition>> outputs = List.of(new Pair<>(Resource.Green, pos3));
        List<GridPosition> pollution = List.of(pos3);



        boolean result = processor.activateCard(cardBasicPollutionEffect, grid, inputs, outputs, pollution);
        assertTrue(result);

        //card should have 1 pollution
        assertEquals(1, cardBasicPollutionEffect.getPollutionOnCard());
        assertFalse(cardBasicPollutionEffect.isBlockedByPollution());

        //add another pollution (max 1 pollutionSpaces, so next should block)
        List<GridPosition> morePollution = List.of(pos3);
        processor.activateCard(cardBasicPollutionEffect, grid, outputs, List.of(new Pair<>(Resource.Green,pos3)), morePollution);

        assertTrue(cardBasicPollutionEffect.isBlockedByPollution());
    }

    /*

    Effect does not match

     */

    @Test
    public void testActivateCardEffectDoesNotMatchReturnsFalse() {
        // cardBasic ArbitraryBasic expects 1 input, 1 green output, 0 pollution
        List<Pair<Resource, GridPosition>> inputs = List.of(new Pair<>(Resource.Red, pos1));
        List<Pair<Resource, GridPosition>> outputs = List.of(new Pair<>(Resource.Red, pos1)); // wrong output
        List<GridPosition> pollution = List.of();

        assertFalse(processor.activateCard(cardBasic, grid, inputs, outputs, pollution));
    }
}
