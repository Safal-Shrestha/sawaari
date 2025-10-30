package com.sawari.dev.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.sawari.dev.model.Entry;
import com.sawari.dev.repository.EntryRepository;

@RestController
@RequestMapping("/api")
public class GetEntryController {

    private final EntryRepository entryRepository;

    public GetEntryController(EntryRepository entryRepository) {
        this.entryRepository = entryRepository;
    }

    // GET
    @GetMapping("/entryInfo")
    public List<Entry> getAllEntry() {
        return entryRepository.findAll();
    }
}
