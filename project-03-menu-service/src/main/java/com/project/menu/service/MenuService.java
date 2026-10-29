package com.project.menu.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.project.menu.entity.Menu;
import com.project.menu.repository.MenuRepository;

@Service
public class MenuService {

    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    // Create menu item
    public Menu createMenu(Menu menu) {
        return menuRepository.save(menu);
    }

    // Get all menu items
    public List<Menu> getAllMenus() {
        return menuRepository.findAll();
    }

    // Get menu item by ID
    public Menu getMenuById(Long id) {
        return menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));
    }

    // Update menu item
    public Menu updateMenu(Long id, Menu menu) {

        Menu existingMenu = menuRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        existingMenu.setName(menu.getName());
        existingMenu.setDescription(menu.getDescription());
        existingMenu.setPrice(menu.getPrice());
        existingMenu.setCategory(menu.getCategory());
        existingMenu.setAvailable(menu.getAvailable());

        return menuRepository.save(existingMenu);
    }

    // Delete menu item
    public void deleteMenu(Long id) {

        if (!menuRepository.existsById(id)) {
            throw new RuntimeException("Menu item not found");
        }

        menuRepository.deleteById(id);
    }
}