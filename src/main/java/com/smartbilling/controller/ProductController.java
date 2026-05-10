package com.smartbilling.controller;

import com.smartbilling.model.Product;
import com.smartbilling.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.List;

/**
 * Controller for product management operations.
 * Handles CRUD operations and search for products.
 */
@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    /**
     * List all products with optional search.
     */
    @GetMapping
    public String listProducts(@RequestParam(required = false) String search, Model model) {
        List<Product> products;
        if (search != null && !search.trim().isEmpty()) {
            products = productService.searchProducts(search);
            model.addAttribute("search", search);
        } else {
            products = productService.getAllProducts();
        }
        model.addAttribute("products", products);
        model.addAttribute("activePage", "products");
        return "products";
    }

    /**
     * Show the add product form.
     */
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("activePage", "products");
        return "add-product";
    }

    /**
     * Process the add product form submission.
     */
    @PostMapping("/add")
    public String addProduct(@RequestParam String name,
                              @RequestParam(required = false) String description,
                              @RequestParam BigDecimal price,
                              @RequestParam BigDecimal gstPercentage,
                              @RequestParam int stockQuantity,
                              RedirectAttributes redirectAttributes,
                              Model model) {
        try {
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setGstPercentage(gstPercentage);
            product.setStockQuantity(stockQuantity);

            productService.addProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product added successfully!");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            Product product = new Product();
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setGstPercentage(gstPercentage);
            product.setStockQuantity(stockQuantity);
            model.addAttribute("product", product);
            model.addAttribute("activePage", "products");
            return "add-product";
        }
    }

    /**
     * Show the edit product form.
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Product product = productService.getProductById(id);
            model.addAttribute("product", product);
            model.addAttribute("activePage", "products");
            return "edit-product";
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Product not found");
            return "redirect:/products";
        }
    }

    /**
     * Process the edit product form submission.
     */
    @PostMapping("/edit/{id}")
    public String updateProduct(@PathVariable int id,
                                 @RequestParam String name,
                                 @RequestParam(required = false) String description,
                                 @RequestParam BigDecimal price,
                                 @RequestParam BigDecimal gstPercentage,
                                 @RequestParam int stockQuantity,
                                 RedirectAttributes redirectAttributes,
                                 Model model) {
        try {
            Product product = new Product();
            product.setId(id);
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setGstPercentage(gstPercentage);
            product.setStockQuantity(stockQuantity);

            productService.updateProduct(product);
            redirectAttributes.addFlashAttribute("success", "Product updated successfully!");
            return "redirect:/products";
        } catch (IllegalArgumentException e) {
            model.addAttribute("error", e.getMessage());
            Product product = new Product();
            product.setId(id);
            product.setName(name);
            product.setDescription(description);
            product.setPrice(price);
            product.setGstPercentage(gstPercentage);
            product.setStockQuantity(stockQuantity);
            model.addAttribute("product", product);
            model.addAttribute("activePage", "products");
            return "edit-product";
        }
    }

    /**
     * Delete a product.
     */
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProduct(id);
            redirectAttributes.addFlashAttribute("success", "Product deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Cannot delete product. It may be referenced in existing invoices.");
        }
        return "redirect:/products";
    }

    /**
     * REST endpoint to get product details as JSON (used by billing page AJAX).
     */
    @GetMapping("/api/{id}")
    @ResponseBody
    public Product getProductJson(@PathVariable int id) {
        return productService.getProductById(id);
    }
}
