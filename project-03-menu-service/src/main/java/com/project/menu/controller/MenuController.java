package com.project.menu.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.project.menu.entity.Menu;
import com.project.menu.service.MenuService;

@RestController
@RequestMapping("/api/menus")
public class MenuController {

    private final MenuService menuService;

    public MenuController(MenuService menuService) {
        this.menuService = menuService;
    }

    // Create menu
    @PostMapping
    public Menu createMenu(@RequestBody Menu menu) {
        return menuService.createMenu(menu);
    }

    // Get all menus
    @GetMapping
    public List<Menu> getAllMenus() {
        return menuService.getAllMenus();
    }

    // Get menu by ID
    @GetMapping("/{id}")
    public Menu getMenuById(@PathVariable Long id) {
        return menuService.getMenuById(id);
    }

    // Update menu
    @PutMapping("/{id}")
    public Menu updateMenu(
            @PathVariable Long id,
            @RequestBody Menu menu) {

        return menuService.updateMenu(id, menu);
    }

    // Delete menu
    @DeleteMapping("/{id}")
    public String deleteMenu(@PathVariable Long id) {

        menuService.deleteMenu(id);

        return "Menu item deleted successfully";
    }
}