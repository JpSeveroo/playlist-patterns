package com.playlist.adapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.playlist.adapter.external.LegacyVinylCatalog;
import com.playlist.core.Track;

/**
 * Adapter que converte os registros do {@link LegacyVinylCatalog} para
 * {@link Track}.
 */
public class VinylCatalogAdapter implements TrackCatalog {

  private final LegacyVinylCatalog legacyCatalog;

  /**
   * Cria o adapter em cima do sistema legado.
   *
   * @param legacyCatalog catálogo legado a ser adaptado. Não pode ser nulo.
   * @throws IllegalArgumentException se o catálogo for nulo.
   */
  public VinylCatalogAdapter(LegacyVinylCatalog legacyCatalog) {
    if (legacyCatalog == null) {
      throw new IllegalArgumentException("Catalogo vazio");
    }

    this.legacyCatalog = legacyCatalog;
  }

  private String capitalizado(String texto) {
    if (texto == null || texto.isBlank()) {
      return "";
    }

    List<Character> lista = new ArrayList<>();

    for (int i = 0; i < texto.length(); i++) {

      char caractere = texto.charAt(i);

      if (i == 0 || texto.charAt(i - 1) == ' ') {
        lista.add(Character.toUpperCase(caractere));
      } else {
        lista.add(Character.toLowerCase(caractere));
      }
    }

    String resultado = "";

    for (Character character : lista) {
      resultado += character;
    }
    return resultado;
  }

  private String inverter(String texto) {
    String[] partes = texto.split(",");
    if (partes.length != 2) {
      return texto.trim();
    }
    return partes[1].trim() + " " + partes[0].trim();
  }

  private Integer converterSegundos(String duracao) {
    try {
      int ms = Integer.parseInt(duracao.trim());
      if (ms < 0) {
        return null;
      }
      return ms / 1000;
    } catch (NumberFormatException e) {
      return null;
    }
  }

  private Boolean verificaPremium(String caractere) {
    if (caractere == null) {
      return false;
    }
    return caractere.trim().equalsIgnoreCase("y");
  }

  private Track converterLinha(String linha) {
    if (linha == null || linha.isBlank()) {
      return null;
    }

    String[] partes = linha.split("\\|", -1);

    if (partes.length != 5) {
      return null;
    }

    String id = partes[0].trim();
    if (id.isEmpty()) {
      return null;
    }

    String title = capitalizado(partes[1].trim());
    if (title.isEmpty()) {
      return null;
    }

    String artist = capitalizado(inverter(partes[2]));
    Integer durationSeconds = converterSegundos(partes[3]);
    if (durationSeconds == null) {
      return null;
    }
    Boolean premium = verificaPremium(partes[4]);

    return new Track(id, title, artist, durationSeconds, premium);
  }

  @Override
  public List<Track> findAll() {
    List<Track> tracks = new ArrayList<>();

    for (String linha : this.legacyCatalog.fetchAllRecords()) {
      Track track = converterLinha(linha);

      if (track != null) {
        tracks.add(track);
      }
    }
    return tracks;
  }

  @Override
  public Optional<Track> findById(String id) {
    if (id == null || id.isBlank()) {
      return Optional.empty();
    }
    String resultado = this.legacyCatalog.findRecordByCatalogNumber(id.trim());
    return Optional.ofNullable(converterLinha(resultado));
  }
}