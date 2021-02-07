package com.example.demo.note;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NoteController {
	
    private final NoteRepository noteRepository;
    private static final String NOTE_URL = "/note";

    @GetMapping(NOTE_URL)
    public ResponseEntity<List<Note>> getAllNotes() {
        return ResponseEntity.ok(noteRepository.findAll());
    }
	
    @PostMapping(NOTE_URL)
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note) throws URISyntaxException {
        Note saved = noteRepository.save(note);
        return ResponseEntity
                .created(new URI(NOTE_URL + "/" + saved.getId()))
                .body(saved);
    }

}
