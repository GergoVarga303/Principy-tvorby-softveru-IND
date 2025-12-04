import org.TerraFutura.*;

import org.junit.Before;
import org.junit.Test;
import java.util.*;
import java.util.AbstractMap.SimpleEntry;

import static org.junit.Assert.*;


public class GameIntegrationTest {
    private Pile pileI;
    private Pile pileII;
    private GameObserver gameObserver;


    private List<Pair<ActivationPattern,ActivationPattern>> activationPatterns;
    private List<Pair<ScoringMethod,ScoringMethod>> scoringMethods;

    private Game game;


    @Before
    public void setup() {
//        //observer for testing
//        gameObserver = new GameObserver() {
//            @Override
//            public void notifyAll(Map<Integer, String> newState) {
//                //for testing, we can just store the last state
//                System.out.println("Game state updated: " + newState);
//            }
//        };

        //create mock cards for piles
        Effect simpleEffect = new ArbitraryBasic(0, List.of(Resource.Green), 0);
        List<Card> deck1Cards = new ArrayList<>();
        List<Card> deck2Cards = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            deck1Cards.add(new Card(simpleEffect, null, 1));
            deck2Cards.add(new Card(simpleEffect, null, 1));
        }

        pileI = new Pile(deck1Cards);
        pileII = new Pile(deck2Cards);

        int numberOfPlayers = 2;

        //create activation patterns for every player
        activationPatterns = new ArrayList<>();
        scoringMethods = new ArrayList<>();

        for (int i = 0; i < numberOfPlayers; i++) {
            //pattern 1: cross pattern
            List<SimpleEntry<Integer, Integer>> pattern1 = Arrays.asList(
                    new SimpleEntry<>(0, 0),
                    new SimpleEntry<>(0, 1),
                    new SimpleEntry<>(0, -1)
            );

            //pattern 2: square pattern
            List<SimpleEntry<Integer, Integer>> pattern2 = Arrays.asList(
                    new SimpleEntry<>(1, 0),
                    new SimpleEntry<>(-1, 0),
                    new SimpleEntry<>(0, 1)
            );


            ActivationPattern ap1 = new ActivationPattern(pattern1);
            ActivationPattern ap2 = new ActivationPattern(pattern2);
            activationPatterns.add(new Pair<>(ap1, ap2));

            //scoring methods
            ScoringMethod sm1 = new ScoringMethod(new Pair<>(
                    Arrays.asList(Resource.Green, Resource.Green, Resource.Green),
                    5
            ));
            ScoringMethod sm2 = new ScoringMethod(new Pair<>(
                    Arrays.asList(Resource.Red, Resource.Yellow, Resource.Bulb),
                    8
            ));
            scoringMethods.add(new Pair<>(sm1, sm2));
        }


        game = new Game(numberOfPlayers, pileI, pileII, gameObserver,
                activationPatterns, scoringMethods);

        //we give every player a starting card
        Player[] players = game.getPlayers();
        for(int i = 0; i < numberOfPlayers; i++){
            players[i].getGrid().putCard(GridPosition.START_P33,new Card(new ArbitraryBasic(0,List.of(Resource.Money),0),null,0));
        }
    }
    //helper methods
    private GridPosition getNextGridPosition(int player) {
        //return different positions for different players
        switch (player) {
            case 0: return GridPosition.P23;
            case 1: return GridPosition.P34;
            default: return GridPosition.P23;
        }
    }


    /*

    Take card, Activate Card, finish turn

     */
    @Test
    public void testCompleteGameFlow() {
        //initial state should be TakeCardNoCardDiscarded
        assertEquals(GameState.TakeCardNoCardDiscarded, game.getState());
        assertEquals(0, game.getOnTurn());
        assertEquals(0, game.getTurnNumber());

        //player0 takes card
        CardSource source = new CardSource(Deck.I, 0);
        GridPosition position = GridPosition.P23; // Position near center

        assertTrue(game.takeCard(0, source, position));

        //state should be ActivateCard
        assertEquals(GameState.ActivateCard, game.getState());

        //try to activate Card
        List<Pair<Resource, GridPosition>> inputs = Collections.emptyList();
        List<Pair<Resource, GridPosition>> outputs = Arrays.asList(
                new Pair<>(Resource.Green, position)
        );
        List<GridPosition> pollution = Collections.emptyList();

        assertTrue(game.activateCard(0, position, inputs, outputs, pollution,
                Optional.empty(), Optional.empty()));

        //finish turn
        assertTrue(game.turnFinished(0));

        //state should be back to TakeCardNoCardDiscarded for player1
        assertEquals(GameState.TakeCardNoCardDiscarded, game.getState());
        assertEquals(1, game.getOnTurn());
        assertEquals(0, game.getTurnNumber());
    }

    /*

    Invalid player

     */
    @Test
    public void testInvalidPlayerActions() {
        //player 1 tries to take card when its player0s turn
        CardSource source = new CardSource(Deck.I, 0);
        GridPosition position = GridPosition.P23;

        assertFalse(game.takeCard(1, source, position));
        assertEquals(0, game.getOnTurn());
    }

    @Test
    public  void testDiscardCardFlow() {
        //initial state should be TakeCardNoCardDiscarded
        assertEquals(GameState.TakeCardNoCardDiscarded, game.getState());

        //player 0 discards last card from deck
        assertTrue(game.discardLastCardFromDeck(0, Deck.I));

        //state should now be TakeCardCardDiscarded
        assertEquals(GameState.TakeCardCardDiscarded, game.getState());

        //player 0 can now take a card
        CardSource source = new CardSource(Deck.I, 0);
        GridPosition position = GridPosition.P23;

        assertTrue(game.takeCard(0, source, position));
        assertEquals(GameState.ActivateCard, game.getState());
    }

    @Test
    public void testMultipleTurnsProgression() {
        //play 2 turns (each player takes one turn)
        for (int player = 0; player < 2; player++) {
            assertEquals(player, game.getOnTurn());

            //take card
            CardSource source = new CardSource(Deck.I, 0);
            GridPosition position = getNextGridPosition(player);

            assertTrue(game.takeCard(player, source, position));
            assertEquals(GameState.ActivateCard, game.getState());

            //activate it
            List<Pair<Resource, GridPosition>> inputs = Collections.emptyList();
            List<Pair<Resource, GridPosition>> outputs = List.of(
                    new Pair<>(Resource.Green, position)
            );

            assertTrue(game.activateCard(player, position, inputs, outputs,
                    Collections.emptyList(), Optional.empty(), Optional.empty()));

            //finish turn
            assertTrue(game.turnFinished(player));
        }

        //after 2 turns we should be back to player 0 with turn number incremented
        assertEquals(0, game.getOnTurn());
        assertEquals(1, game.getTurnNumber());
    }

    @Test
    public  void testNineTurnsTriggersActivationPattern() {
        //simulate 9 full rounds
        for (int turn = 0; turn < 9; turn++) {
            for (int player = 0; player < 2; player++) {
                //skip card placement for simplicity
                //just advance the turn
                game.turnFinished(player);
            }
        }

        //after 9 turns state should be SelectActivationPattern
        assertEquals(GameState.SelectActivationPattern, game.getState());
    }

    @Test
    public void testActivationPatternSelection() {
        //simulate 9 full rounds
        for (int turn = 0; turn < 9; turn++) {
            for (int player = 0; player < 2; player++) {
                //skip card placement for simplicity
                //just advance the turn
                game.turnFinished(player);
            }
        }
        //after 9 turns state should be SelectActivationPattern
        assertEquals(GameState.SelectActivationPattern, game.getState());

        //test selecting activation pattern
        assertTrue(game.selectActivationPattern(0, 1));
        game.activationPatternTurnFinished(0);//we skip activation
        assertEquals(1, game.getOnTurn()); //next players turn
        assertTrue(game.selectActivationPattern(1, 2));
        game.activationPatternTurnFinished(1);
        assertEquals(0, game.getOnTurn()); //back to starting player


        assertEquals(GameState.SelectScoringMethod, game.getState());
    }

    @Test
    public void testActivationPatternActivationFlow() {
        //setup: create activation patterns for 2 players
        List<SimpleEntry<Integer, Integer>> pattern = Arrays.asList(
                new SimpleEntry<>(0, 0),
                new SimpleEntry<>(1, 0)
        );

        ActivationPattern ap = new ActivationPattern(pattern);
        List<Pair<ActivationPattern, ActivationPattern>> testPatterns = Arrays.asList(
                new Pair<>(ap, ap),
                new Pair<>(ap, ap)
        );

        //create scoring methods for 2 players
        List<Pair<ScoringMethod, ScoringMethod>> testMethods = Arrays.asList(
                new Pair<>(new ScoringMethod(new Pair<>(Collections.emptyList(), 0)),
                        new ScoringMethod(new Pair<>(Collections.emptyList(), 0))),
                new Pair<>(new ScoringMethod(new Pair<>(Collections.emptyList(), 0)),
                        new ScoringMethod(new Pair<>(Collections.emptyList(), 0)))
        );

        //create a simple card with an effect
        Effect testEffect = new ArbitraryBasic(0, Arrays.asList(Resource.Green), 0);
        Card testCard = new Card(testEffect, null, 1);

        List<Card> cards = Arrays.asList(testCard, testCard, testCard, testCard);
        Pile pileI = new Pile(new ArrayList<>(cards));
        Pile pileII = new Pile(new ArrayList<>(cards));

        Game testGame = new Game(2, pileI, pileII, gameObserver, testPatterns, testMethods);

        //player0 takes card
        CardSource source = new CardSource(Deck.I, 0);
        assertTrue(testGame.takeCard(0, source, GridPosition.START_P33));

        //finish turn to move to next player
        testGame.turnFinished(0);

        //player1 takes card
        CardSource source2 = new CardSource(Deck.I, 1);
        assertTrue(testGame.takeCard(1, source2, GridPosition.START_P33));
        testGame.turnFinished(1);

        for (int turn = 0; turn < 8; turn++) {
            for (int player = 0; player < 2; player++) {
                //skip card placement for simplicity
                //just advance the turn
                testGame.turnFinished(player);
            }
        }

        //after 9 turns state should be SelectActivationPattern
        assertEquals(GameState.SelectActivationPattern, testGame.getState());

        testGame.selectActivationPattern(0,1);
        boolean res = testGame.activationPatternActivateCard(0,GridPosition.START_P33,
                null,List.of(new Pair<>(Resource.Green,GridPosition.START_P33)),List.of(),
                Optional.empty(),Optional.empty());
        assertTrue(res);
        testGame.activationPatternTurnFinished(0);

        //player1
        testGame.selectActivationPattern(1,1);
        boolean res2 = testGame.activationPatternActivateCard(1,GridPosition.START_P33,
                null,List.of(new Pair<>(Resource.Green,GridPosition.START_P33)),List.of(),
                Optional.empty(),Optional.empty());
        testGame.activationPatternTurnFinished(1);

        assertEquals(GameState.SelectScoringMethod, testGame.getState());

    }

    @Test
    public void testScoringMethodSelection() {
        //simulate 9 full rounds
        for (int turn = 0; turn < 9; turn++) {
            for (int player = 0; player < 2; player++) {
                //skip card placement for simplicity
                //just advance the turn
                game.turnFinished(player);
            }
        }
        //after 9 turns state should be SelectActivationPattern
        assertEquals(GameState.SelectActivationPattern, game.getState());
        //test selecting activation pattern
        assertTrue(game.selectActivationPattern(0, 1));
        game.activationPatternTurnFinished(0);//we skip activation
        assertEquals(1, game.getOnTurn()); //next players turn
        assertTrue(game.selectActivationPattern(1, 2));
        game.activationPatternTurnFinished(1);
        assertEquals(0, game.getOnTurn()); //back to starting player


        assertEquals(GameState.SelectScoringMethod, game.getState());

        //now should be in SelectScoringMethod state

        //test selecting scoring method for player 0
        assertTrue(game.selectScoring(0, 1));
        assertEquals(GameState.SelectScoringMethod, game.getState());
        assertEquals(1, game.getOnTurn());

        //player 1
        assertTrue(game.selectScoring(1, 2));


        //after last player selects scoring state should be Finish
        assertEquals(GameState.Finish, game.getState());
    }

    @Test
    public void testCardPlacementRestrictions() {
        //player 0 takes first card
        CardSource source1 = new CardSource(Deck.I, 0);
        assertTrue(game.takeCard(0, source1, GridPosition.P23));
        game.turnFinished(0);

        //player 1 tries to place card in invalid position (no neighbor)
        CardSource source2 = new CardSource(Deck.I, 0);
        assertFalse(game.takeCard(1, source2, GridPosition.P55)); //it writes move card failed on console

        //player 1 places it correctly
        assertTrue(game.takeCard(1, source2, GridPosition.P34)); //starting cards neighbor
        game.turnFinished(1);
        assertEquals(1,game.getTurnNumber());
    }

    @Test
    public void testPollutionMechanics() {
        //create a card with pollution capacity
        Effect effectWithPollution = new ArbitraryBasic(0, Arrays.asList(Resource.Green), 1);
        Effect effectWithOUTPollution = new ArbitraryBasic(0, Arrays.asList(Resource.Green), 0);
        Card cardWithPollution = new Card(effectWithPollution, null, 1); // 1 pollution space

        List<Card> testDeck = Arrays.asList(cardWithPollution, new Card(effectWithOUTPollution, null, 1),
                new Card(effectWithOUTPollution, null, 1), new Card(effectWithPollution, null, 1)
                );
        Pile testPile = new Pile(new ArrayList<>(testDeck));

        //create patterns and methods for 2 players
        List<Pair<ActivationPattern, ActivationPattern>> twoPlayerPatterns =
                activationPatterns.subList(0, 2);
        List<Pair<ScoringMethod, ScoringMethod>> twoPlayerMethods =
                scoringMethods.subList(0, 2);

        Game testGame = new Game(2, testPile, testPile, gameObserver,
                twoPlayerPatterns, twoPlayerMethods);

        //player 0 takes card
        CardSource source = new CardSource(Deck.I, 0);
        GridPosition position = GridPosition.START_P33;
        assertTrue(testGame.takeCard(0, source, position));

        //activate card with pollution
        List<GridPosition> pollution = Arrays.asList(position);
        List<Pair<Resource, GridPosition>> outputs = Arrays.asList(
                new Pair<>(Resource.Green, position)
        );

        assertTrue(testGame.activateCard(0, position,
                Collections.emptyList(),
                outputs,
                pollution,
                Optional.empty(),
                Optional.empty()));

        //finish turn
        testGame.turnFinished(0);

        //player 1 takes a card
        CardSource source2 = new CardSource(Deck.I, 1);
        GridPosition position2 = GridPosition.START_P33;
        assertTrue(testGame.takeCard(1, source2, position2));

        //player 1 activates their card (no pollution)
        List<Pair<Resource, GridPosition>> outputs2 = Arrays.asList(
                new Pair<>(Resource.Green, position2)
        );

        assertTrue(testGame.activateCard(1, position2,
                null,
                outputs2,
                null,
                Optional.empty(),
                Optional.empty()));
    }

    @Test
    public void testInvalidNumberOfPlayers() {
        //try to create game with 1 player
        assertThrows(IllegalArgumentException.class, () -> {
            new Game(1,
                    new Pile(createTestCards(10)),
                    new Pile(createTestCards(10)),
                    gameObserver,
                    activationPatterns.subList(0, 1),
                    scoringMethods.subList(0, 1));
        });

        //try to create game with 6 players
        assertThrows(IllegalArgumentException.class, () -> {
            //create lists for 6 players
            List<Pair<ActivationPattern, ActivationPattern>> sixPatterns =
                    new ArrayList<>(activationPatterns);
            List<Pair<ScoringMethod, ScoringMethod>> sixMethods =
                    new ArrayList<>(scoringMethods);

            //5 more dummy entries
            for (int i = 0; i < 5; i++) {
                sixPatterns.add(activationPatterns.get(0));
                sixMethods.add(scoringMethods.get(0));
            }

            new Game(6,
                    new Pile(createTestCards(10)),
                    new Pile(createTestCards(10)),
                    gameObserver,
                    sixPatterns,
                    sixMethods);
        });
    }

    //helper
    private List<Card> createTestCards(int count) {
        Effect simpleEffect = new ArbitraryBasic(0, Arrays.asList(Resource.Green), 0);
        List<Card> cards = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            cards.add(new Card(simpleEffect, null, 1));
        }
        return cards;
    }


}
