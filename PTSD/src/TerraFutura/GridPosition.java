package TerraFutura;
//finished
public enum GridPosition {
    //martin
    //!!! P__ treba vnimat prve cislo ako riadok a druhe ako stlpec matice,
    //ktora ma stred v START_P33
    //!!! stred ma na osi suradnice (0,0), a preto P12 vrati x = -1 a y = -2
    //!!! x-ova suradnica teda nekoresponduje s riadkami matice ale stlpcami

    START_P33(0,0),
    P11(-2,-2),
    P12(-1,-2),
    P13(0,-2),
    P14(1,-2),
    P15(2,-2),
    P21(-2,-1),
    P22(-1,-1),
    P23(0,-1),
    P24(1,-1),
    P25(2,-1),
    P31(-2,0),
    P32(-1,0),
    P34(1,0),
    P35(2,0),
    P41(-2,1),
    P42(-1,1),
    P43(0,1),
    P44(1,1),
    P45(2,1),
    P51(-2,2),
    P52(-1,2),
    P53(0,2),
    P54(1,2),
    P55(2,2);

    private final int x;
    private final int y;

    GridPosition(int x, int y)
    {
        this.x = x;
        this.y = y;
    }
    public int getX() { return x; }
    public int getY() { return y; }

}
