import org.TerraFutura.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class ScoringMethodUnitTest {
    @Test
    public void testNoResourcesReturnsZero() {
        ScoringMethod method = new ScoringMethod(new Pair<>(List.of(Resource.Green), 3));

        method.selectThisMethodAndCalculate();
        assertEquals(0, method.getTotal());
    }
    @Test
    public void testOnlyIndividualValues() {
        ScoringMethod method = new ScoringMethod(new Pair<>(Collections.emptyList(), 5));
        method.setAllResources(List.of(
                Resource.Green,   // 1
                Resource.Bulb,    // 5
                Resource.Car,     // 6
                Resource.Pollution // -1
        ));

        method.selectThisMethodAndCalculate();

        int expected = 1 + 5 + 6 - 1;
        assertEquals(expected, method.getTotal());
    }

    @Test
    public void testSimpleCombinationOneSet() {
        //green+red -> 10 points
        ScoringMethod method = new ScoringMethod(
                new Pair<>(List.of(Resource.Green, Resource.Red), 10)
        );

        method.setAllResources(List.of(
                Resource.Green, //+1
                Resource.Red,   //+1
                Resource.Yellow // +1
        ));

        method.selectThisMethodAndCalculate();

        int expected = 10 + 3; // combination + individual(+3)
        assertEquals(expected, method.getTotal());
    }

    @Test
    public void testMultipleCombinations() {
        // Combination = (Green, Green, Red) worth 7 points
        ScoringMethod method = new ScoringMethod(
                new Pair<>(List.of(Resource.Green, Resource.Green, Resource.Red), 7)
        );

        method.setAllResources(List.of(
                Resource.Green,
                Resource.Green,
                Resource.Green,
                Resource.Green,
                Resource.Red,
                Resource.Red
        ));
        //2 combos

        method.selectThisMethodAndCalculate();

        int comboScore = 2 * 7; //2 combos
        int individual = 4*1 + 2*1; //all are worth 1 individually

        assertEquals(comboScore + individual, method.getTotal());
    }

    @Test
    public void testZeroCombinationValueNoComboPoints() {
        ScoringMethod method = new ScoringMethod(
                new Pair<>(List.of(Resource.Green, Resource.Red), 0)
        );

        method.setAllResources(List.of(Resource.Green, Resource.Red));

        method.selectThisMethodAndCalculate();

        //only individual: 1+1
        assertEquals(2, method.getTotal());
    }

    @Test
    public void testPollutionReducesScore() {
        ScoringMethod method = new ScoringMethod(
                new Pair<>(List.of(Resource.Green, Resource.Red), 5)
        );

        method.setAllResources(List.of(
                Resource.Green,    // +1
                Resource.Red,      // +1
                Resource.Pollution // -1
        ));

        method.selectThisMethodAndCalculate();

        int expected = 5 + 1 + 1 - 1;
        assertEquals(expected, method.getTotal());
    }

    @Test
    public void testStateFormat() {
        ScoringMethod method = new ScoringMethod(new Pair<>(List.of(Resource.Green), 3));

        method.setAllResources(List.of(Resource.Green));
        method.selectThisMethodAndCalculate();

        String state = method.state();
        assertTrue(state.contains("ScoringMethod"));
        assertTrue(state.contains("Total points"));
        Integer total = method.getTotal();
        assertTrue(state.contains(total.toString()));
    }

}
