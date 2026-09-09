package com.playlist.composite;

import java.util.List;

import com.playlist.core.Track;

/**
 * Folha do padrão Composite: envolve uma única {@link Track}.
 */
public class TrackItem implements MediaItem {
  private final Track track;
  /**
   * Cria a folha a partir de uma faixa.
   *
   * @param track faixa envolvida. Não pode ser nula.
   * @throws IllegalArgumentException se {@code track} for nula.
   */
  public TrackItem(Track track) {
    if (track == null){
      throw new IllegalArgumentException("A faixa não pode ser nula");
    }
    this.track = track;
  }

  /**
   * Devolve a faixa envolvida por esta folha.
   *
   * @return a faixa original.
   */
  public Track getTrack() {
    return this.track;
  }

  @Override
  public String getName() {
    return this.track.title();
  }

  @Override
  public int getDurationSeconds() {
    return this.track.durationSeconds();
  }

  @Override
  public int getTrackCount() {
    return 1;
  }

  @Override
  public List<Track> flatten() {
    return List.of(this.track);
  }
}
