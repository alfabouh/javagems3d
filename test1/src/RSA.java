import java.math.BigInteger;
import java.security.SecureRandom;

public class RSA {

    private BigInteger p, q, n, phi, e, d;
    private int bitLength = 1024;

    public RSA() {
        generateKeys();
    }

    public void generateKeys() {
        SecureRandom random = new SecureRandom();

        p = BigInteger.probablePrime(bitLength / 2, random);
        q = BigInteger.probablePrime(bitLength / 2, random);

        n = p.multiply(q);
        phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));

        e = BigInteger.valueOf(65537);

        while (!phi.gcd(e).equals(BigInteger.ONE)) {
            e = e.add(BigInteger.TWO);
        }

        d = e.modInverse(phi);
    }

    public BigInteger encrypt(BigInteger message) {
        return message.modPow(e, n);
    }

    public BigInteger decrypt(BigInteger cipher) {
        return cipher.modPow(d, n);
    }

    public BigInteger getE() { return e; }
    public BigInteger getD() { return d; }
    public BigInteger getN() { return n; }
}