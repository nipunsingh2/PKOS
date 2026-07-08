// package com.pkos.backend.config;

// import com.pkos.backend.entity.Note;
// import com.pkos.backend.repository.NoteRepository;
// import org.springframework.boot.CommandLineRunner;
// import org.springframework.stereotype.Component;

// @Component
// public class DataInitializer implements CommandLineRunner {

//     private final NoteRepository noteRepository;

//     public DataInitializer(NoteRepository noteRepository) {
//         this.noteRepository = noteRepository;
//     }

//     @Override
//     public void run(String... args) {

//         if (noteRepository.count() == 0) {

//             Note note = new Note(
//                     "Java Basics",
//                     "Object-Oriented Programming Concepts"
//             );

//             noteRepository.save(note);

//             System.out.println("Sample note inserted successfully!");

//         } 
//         else{

//             System.out.println("Notes already exist. Skipping sample data.");
//         }
//     }
// }