package com.playlist.decorator;

/**
 * Efeito que aplica uma rampa linear de volume nas primeiras amostras.
 */
public final class FadeInEffect extends AudioEffect {

  private final int sampleCount;

  /**
   * Cria o efeito de fade in.
   *
   * @param wrapped áudio decorado.
   * @param sampleCount quantidade de amostras usadas na rampa.
   */
  public FadeInEffect(AudioTrack wrapped, int sampleCount) {
    super(wrapped);
    this.sampleCount = sampleCount;
  }

  @Override
  protected String describe() {
    return String.format("fadeIn(%d)", this.sampleCount);
  }

  @Override
  public double[] getSamples() {
    double[] lista1 = wrapped.getSamples();
    double[] listaFinal = new double[lista1.length];

    if (sampleCount <= 0) {
      return lista1.clone();
    }

    for (int i = 0; i < lista1.length; i++) {
      if (i < sampleCount) {
        listaFinal[i] = lista1[i] * ((double) i / sampleCount);
      } else {
        listaFinal[i] = lista1[i];
      }
    }
    return listaFinal;
  }
}
