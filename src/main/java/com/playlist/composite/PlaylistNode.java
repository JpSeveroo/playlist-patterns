package com.playlist.composite;


import java.util.ArrayList;
import java.util.List;

import com.playlist.core.Track;

/**
 * Composite do padrão Composite: uma playlist que pode conter faixas e outras playlists.
 */
public class PlaylistNode implements MediaItem {

  private List<MediaItem> itens = new ArrayList<>();
  private final String name;
  /**
   * Cria uma playlist vazia.
   *
   * @param name nome da playlist. Não pode ser nulo nem em branco.
   * @throws IllegalArgumentException se o nome for nulo ou em branco.
   */
  public PlaylistNode(String name) {
    if (name == null || name.isBlank()) {
        throw new IllegalArgumentException("Nome vazio");
    }
      this.name = name;
  }

  /**
   * Adiciona um item ao final da playlist.
   *
   * @param item item a ser adicionado.
   * @return a própria playlist, permitindo encadear chamadas.
   * @throws IllegalArgumentException se o item for nulo, for a própria playlist ou contiver a própria playlist (o que criaria um ciclo).
   */
  public PlaylistNode add(MediaItem item) {
  
    if (item == null || item == this){
      throw new IllegalArgumentException("Erro");
    }
    if (item instanceof PlaylistNode subPlaylist){
      if (subPlaylist.contains(this)){
        throw new IllegalArgumentException("Erro");
      }
    }
    this.itens.add(item);
    return this;
  }

  /**
   * Remove um filho direto da playlist.
   *
   * @param item item a ser removido.
   * @return {@code true} se o item era filho direto e foi removido.
   */
  public boolean remove(MediaItem item) {
    return this.itens.remove(item);
  }

  /**
   * Lista os filhos diretos da playlist.
   *
   * @return uma lista imutável com os filhos, na ordem de inserção.
   */
  public List<MediaItem> getChildren() {
    return List.copyOf(itens);
  }

  /**
   * Verifica se o item está em qualquer nível abaixo desta playlist.
   *
   * @param item item procurado.
   * @return {@code true} se o item for filho direto ou descendente.
   */
  public boolean contains(MediaItem item) {
    for (MediaItem i : itens){
      if (i.equals(item)){
        return true;
      }
      if (i instanceof PlaylistNode subPlaylist){
        if (subPlaylist.contains(item)){
          return true;
        }
      }
    }
    return false;
  }

  @Override
  public String getName() {
      return this.name;
  }

  @Override
  public int getDurationSeconds() {
    int total = 0;
    for (MediaItem i : itens){
      total += i.getDurationSeconds();
    }
    return total;
  }

  @Override
  public int getTrackCount() {
    int total = 0;
    for (MediaItem i : itens){
      total += i.getTrackCount();
    }
    return total;
  }

  @Override
  public List<Track> flatten() {
      List<Track> lista = new ArrayList<>();

      for (MediaItem i : itens) {
          lista.addAll(i.flatten());
      }

      return lista;
  }
}
