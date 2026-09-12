package com.playlist.facade;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.playlist.adapter.TrackCatalog;
import com.playlist.composite.PlaylistNode;
import com.playlist.composite.TrackItem;
import com.playlist.core.Subscription;
import com.playlist.core.Track;
import com.playlist.core.TrackNotFoundException;
import com.playlist.decorator.AudioTrack;
import com.playlist.decorator.FadeInEffect;
import com.playlist.decorator.RawAudioTrack;
import com.playlist.decorator.VolumeEffect;
import com.playlist.proxy.AudioStream;
import com.playlist.proxy.ProtectedAudioStreamProxy;

/**
 * Fachada que esconde do mundo externo a colaboração entre catálogo, playlists,
 * streams protegidos e efeitos de áudio.
 *
 * <p>Quem usa a Playlist precisa conhecer apenas esta classe.
 */
public class PlaylistFacade {
  private final TrackCatalog catalog;
  private final Subscription plan;
  private final Map<String, AudioStream> cache;

  /**
   * Monta a fachada.
   *
   * @param catalog catálogo de faixas já adaptado.
   * @param plan plano de assinatura de quem está usando o sistema.
   * @throws IllegalArgumentException se qualquer argumento for nulo.
   */
  public PlaylistFacade(TrackCatalog catalog, Subscription plan) {

    if (catalog == null || plan == null) {
      throw new IllegalArgumentException("Pode nn");
    }
    this.catalog = catalog;
    this.plan = plan;
    this.cache = new HashMap<>();
  }

  /**
   * Monta uma playlist com todas as faixas do catálogo, na ordem em que o
   * catálogo as devolve.
   *
   * @param name nome da playlist criada.
   * @return a playlist preenchida.
   */
  public PlaylistNode buildLibrary(String name) {
    PlaylistNode library = new PlaylistNode(name);

    List<Track> lista = catalog.findAll();
    for (Track track : lista) {
      library.add(new TrackItem(track));
    }
    return library;
  }

  /**
   * Devolve os bytes de áudio de uma faixa, respeitando o plano de assinatura.
   *
   * @param trackId identificador da faixa.
   * @return os bytes do áudio.
   * @throws TrackNotFoundException se a faixa não existir no catálogo.
   */
  public byte[] listen(String trackId) {
    if (trackId == null) {
      throw new TrackNotFoundException("Faixa não encontrada: id nulo");
    }

    if (!cache.containsKey(trackId)) {
      Track track = catalog.findById(trackId)
          .orElseThrow(() -> new TrackNotFoundException("Faixa não encontrada: " + trackId));

      // Cria o proxy protegido associando a faixa ao plano do usuário
      AudioStream stream = new ProtectedAudioStreamProxy(track, plan);
      cache.put(trackId, stream);
    }

    return cache.get(trackId).readBytes();
  }

  /**
   * Monta uma prévia da faixa com volume ajustado e fade in.
   *
   * @param trackId identificador da faixa.
   * @param volume fator de volume aplicado primeiro.
   * @param fadeInSamples quantidade de amostras do fade in, aplicado depois.
   * @return o áudio já decorado.
   * @throws TrackNotFoundException se a faixa não existir no catálogo.
   */
  public AudioTrack preview(String trackId, double volume, int fadeInSamples) {
    if (trackId == null) {
      throw new TrackNotFoundException("Faixa não encontrada: id nulo");
    }

    Track track = catalog.findById(trackId)
        .orElseThrow(() -> new TrackNotFoundException("Faixa não encontrada: " + trackId));

    byte[] bytes = listen(trackId);
    double[] samples = new double[bytes.length];
    for (int i = 0; i < bytes.length; i++) {
      samples[i] = bytes[i] / 128.0;
    }

    AudioTrack audio = new RawAudioTrack(track.title(), samples);
    audio = new VolumeEffect(audio, volume);
    audio = new FadeInEffect(audio, fadeInSamples);

    return audio;
  }
}