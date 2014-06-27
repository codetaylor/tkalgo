package com.sudoplay.tkalgo;

import java.util.Random;

/**
 * Not thread safe.
 * <p>
 * 
 * @author Jason Taylor
 * 
 */
@SuppressWarnings("serial")
public class XORShiftRandom extends Random {

  private long seed = System.nanoTime();

  public XORShiftRandom() {
    //
  }

  public XORShiftRandom(long seed) {
    this.seed = seed;
  }

  protected int next(int nbits) {
    // not thread-safe!
    long x = this.seed;
    x ^= (x << 21);
    x ^= (x >>> 35);
    x ^= (x << 4);
    this.seed = x;
    x &= ((1L << nbits) - 1);
    return (int) x;
  }

  @Override
  public long nextLong() {
    long x = this.seed;
    x ^= (x << 21);
    x ^= (x >>> 35);
    x ^= (x << 4);
    this.seed = x;
    return (long) x;
  }

}
