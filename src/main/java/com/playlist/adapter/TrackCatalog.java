package com.playlist.adapter;

import java.util.List;
import java.util.Optional;

import com.playlist.core.Track;

/**
 * Alvo (Target) do padrão Adapter: a interface de catálogo que Playlist usa internamente.
 *
 * <p>Todo o restante do sistema depende apenas desta interface — nunca do
 * formato do sistema legado.</p>
 */
public interface TrackCatalog {

  /**
   * Lista todas as faixas válidas do catálogo.
   *
   * @return as faixas já convertidas para o modelo interno.
   */
  List<Track> findAll();

  /**
   * Busca uma faixa pelo identificador.
   *
   * @param id identificador da faixa (ex.: {@code "VNL-0001"}).
   * @return a faixa encontrada ou {@link Optional#empty()}.
   */
  Optional<Track> findById(String id);
}
