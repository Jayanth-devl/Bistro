package com.project.menu.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.menu.entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {

}