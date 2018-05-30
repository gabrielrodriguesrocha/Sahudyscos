package com.sahudyscos.web.repository;

import com.sahudyscos.web.entity.Album;

import org.springframework.data.repository.PagingAndSortingRepository;

// This will be AUTO IMPLEMENTED by Spring into a Bean called albumRepository
// CRUD refers Create, Read, Update, Delete

public interface PageAlbumRepository extends PagingAndSortingRepository<Album, Long> {
}