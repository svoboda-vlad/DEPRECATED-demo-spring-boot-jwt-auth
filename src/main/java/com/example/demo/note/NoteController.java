package com.example.demo.note;

import java.util.List;

import javax.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class NoteController {
	
    private final NoteRepository noteRepository;

    @GetMapping("/note")
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }
	
    @PostMapping("/note")
    public Note createNote(@Valid @RequestBody Note note) {
        return noteRepository.save(note);
    }

}
