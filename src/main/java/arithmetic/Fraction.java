package arithmetic;

import java.math.BigInteger;

public class Fraction {
    public BigInteger up;
    public Integer down;
    public Integer pow;

    public Fraction(BigInteger up, Integer down, Integer pow) {
        this.up = up;
        this.pow = pow;
        this.down = down;
    }

    @Override
    public String toString() {
        return up + "/" + down + "**" + pow;
    }
}
