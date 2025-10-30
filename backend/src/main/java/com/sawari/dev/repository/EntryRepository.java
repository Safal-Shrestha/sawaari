package com.sawari.dev.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.sawari.dev.model.Entry;

public interface EntryRepository extends JpaRepository<Entry, Long> {
}
