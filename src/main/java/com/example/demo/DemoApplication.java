package com.example.demo;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import com.example.demo.note.Note;
import com.example.demo.note.NoteRepository;

@SpringBootApplication
public class DemoApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	
	@Bean
	public CommandLineRunner dataLoader(NoteRepository noteRepo) {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				Note note1 = new Note(0, "note 123");
				Note note2 = new Note(0, "note 124");
				List<Note> notes = new ArrayList<Note>();
				notes.add(note1);
				notes.add(note2);
				if (noteRepo.findAll().size() == 0) {
					noteRepo.saveAll(notes);
				}
			}
		};
	}	

}