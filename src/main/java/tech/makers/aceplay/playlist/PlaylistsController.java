package tech.makers.aceplay.playlist;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import tech.makers.aceplay.track.Track;
import tech.makers.aceplay.track.TrackRepository;

import static org.springframework.http.HttpStatus.NOT_FOUND;

// https://www.youtube.com/watch?v=vreyOZxdb5Y&t=0s
@RestController
public class PlaylistsController {
  @Autowired private PlaylistRepository playlistRepository;

  @Autowired private TrackRepository trackRepository;

  @GetMapping("/api/playlists")
  public Iterable<Playlist> playlists() {
    return playlistRepository.findAll();
  }

  // Need to refactor this into new class
  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<String> handleError(RuntimeException ex) {
    return new ResponseEntity<>(ex.getMessage(), new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @PostMapping("/api/playlists")
  public Playlist create(@RequestBody Playlist playlist) {
    if (playlist.getName() == "")
      throw new RuntimeException("Playlist name is empty.");

    return playlistRepository.save(playlist);
  }

  @GetMapping("/api/playlists/{id}")
  public Playlist get(@PathVariable Long id) {
    return playlistRepository.findById(id)
        .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No playlist exists with id " + id));
  }

  @PutMapping("/api/playlists/{id}/tracks")
  public Track addTrack(@PathVariable Long id, @RequestBody TrackIdentifierDto trackIdentifierDto) {
    Playlist playlist = playlistRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No playlist exists with id " + id));
    Track track = trackRepository.findById(trackIdentifierDto.getId())
            .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "No track exists with id " + trackIdentifierDto.getId()));
    playlist.getTracks().add(track);
    playlistRepository.save(playlist);
    return track;
  }
}
