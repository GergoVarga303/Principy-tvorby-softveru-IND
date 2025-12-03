import org.TerraFutura.*;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.*;

public class CardAndEffectTest {
    @Test
    public void CheckWorksForLowerAndUpperTransFixed(){
        TransformationFixed generate = new TransformationFixed(
                List.of(),
                List.of(Resource.Green),
                0
        );

        TransformationFixed effect2 = new TransformationFixed(
                List.of(Resource.Money),
                List.of(Resource.Red),
                1
        );

        Card card = new Card(generate,effect2,1);
        assertTrue(card.check(List.of(),List.of(Resource.Green),0));
        assertFalse(card.check(List.of(),List.of(Resource.Green),1));

        assertTrue(card.checkLower(List.of(Resource.Money), List.of(Resource.Red),1));
        assertFalse(card.checkLower(List.of(Resource.Money),List.of(Resource.Green),1));
    }

    @Test
    public void arbitraryBasicValidCheck() {
        ArbitraryBasic effect = new ArbitraryBasic(
                2,
                List.of(Resource.Green, Resource.Red),
                1
        );

        List<Resource> input = List.of(Resource.Green, Resource.Red);
        List<Resource> output = List.of(Resource.Red, Resource.Green);
        Card card = new Card(effect,null,1);

        assertTrue(card.check(input, output, 1));
        assertFalse(card.check(input, output, 0));  // wrong pollution
        assertFalse(card.check(List.of(Resource.Green), output, 1)); // wrong input size
        assertFalse(card.check(List.of(Resource.Green, Resource.Money), output, 1)); // invalid basic
        assertFalse(card.check(input, output, 2));  // wrong pollution
    }

    @Test
    public void arbitraryProductValidCheck() {
        ArbitraryProduct effect = new ArbitraryProduct();
        Card card = new Card(effect,null,2);

        // valid conversions
        assertTrue(card.check(List.of(Resource.Car), List.of(Resource.Bulb), 0));
        assertTrue(card.check(List.of(Resource.Gear), List.of(Resource.Car), 0));

        // invalid conversions
        assertFalse(card.check(List.of(Resource.Car, Resource.Bulb), List.of(Resource.Gear), 0)); // multiple inputs
        assertFalse(card.check(List.of(Resource.Car), List.of(Resource.Bulb, Resource.Gear), 0)); // multiple outputs
        assertFalse(card.check(List.of(Resource.Car), List.of(Resource.Bulb), 1)); // pollution != 0
        assertFalse(card.check(List.of(Resource.Green), List.of(Resource.Car), 0)); // non-product input
    }

    /*

    Composite

     */
    @Test
    public void compositeTestWithCard() {
        ArbitraryBasic basic = new ArbitraryBasic(1, List.of(Resource.Green), 0);
        ArbitraryProduct product = new ArbitraryProduct();

        EffectOr composite = new EffectOr();
        composite.addEffect(basic);
        composite.addEffect(product);

        Card card = new Card(composite,null,2);

        assertTrue(card.check(List.of(Resource.Green), List.of(Resource.Green), 0));
        assertTrue(card.check(List.of(Resource.Red), List.of(Resource.Green), 0));

        assertFalse(card.check(List.of(Resource.Car), List.of(Resource.Green), 0));
        assertTrue(card.check(List.of(Resource.Car), List.of(Resource.Bulb), 0));

        assertFalse(card.check(List.of(Resource.Money),List.of(Resource.Red),0));
    }

    /*
        State
         */
    @Test
    public void cardStateTest(){
        ArbitraryBasic basic = new ArbitraryBasic(1, List.of(Resource.Green), 0);
        ArbitraryProduct product = new ArbitraryProduct();
        EffectOr composite = new EffectOr();
        composite.addEffect(basic);
        composite.addEffect(product);

        Card card = new Card(null,composite,2);

        String state = card.state();
        assertTrue(state.contains("ArbitraryBasic"));
        assertTrue(state.contains("any product"));
        assertTrue(state.contains("OR"));
    }

}
