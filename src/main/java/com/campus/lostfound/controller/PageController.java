package com.campus.lostfound.controller;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.repository.ItemRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

/** Handles every page of the website. */
@Controller
public class PageController {

    private final ItemRepository repo;

    public PageController(ItemRepository repo) {
        this.repo = repo;
    }

    // ---------- Home ----------

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("lostCount", repo.countLostItems());
        model.addAttribute("foundCount", repo.countAvailableFoundItems());
        model.addAttribute("pendingCount", repo.countPendingClaims());
        return "index";
    }

    // ---------- Lost items ----------

    @GetMapping("/lost")
    public String lostItems(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("items", repo.findLostItems(q.trim()));
        model.addAttribute("q", q);
        return "lost-items";
    }

    @GetMapping("/lost/new")
    public String lostForm(Model model) {
        model.addAttribute("heading", "Report a Lost Item");
        model.addAttribute("intro", "Tell us what you lost and where you last saw it.");
        model.addAttribute("action", "/lost");
        model.addAttribute("back", "/lost");
        return "report-form";
    }

    @PostMapping("/lost")
    public String submitLostItem(@RequestParam String itemName,
                                 @RequestParam(defaultValue = "") String description,
                                 @RequestParam(defaultValue = "") String location,
                                 RedirectAttributes redirect) {
        String problem = checkItemFields(itemName, description, location);
        if (problem != null) {
            redirect.addFlashAttribute("error", problem);
            return "redirect:/lost/new";
        }
        repo.addLostItem(itemName.trim(), description.trim(), location.trim());
        redirect.addFlashAttribute("message", "Lost item reported successfully.");
        return "redirect:/lost";
    }

    // ---------- Found items ----------

    @GetMapping("/found")
    public String foundItems(@RequestParam(defaultValue = "") String q, Model model) {
        model.addAttribute("items", repo.findFoundItems(q.trim()));
        model.addAttribute("q", q);
        return "found-items";
    }

    @GetMapping("/found/new")
    public String foundForm(Model model) {
        model.addAttribute("heading", "Report a Found Item");
        model.addAttribute("intro", "Tell us what you found and where you found it.");
        model.addAttribute("action", "/found");
        model.addAttribute("back", "/found");
        return "report-form";
    }

    @PostMapping("/found")
    public String submitFoundItem(@RequestParam String itemName,
                                  @RequestParam(defaultValue = "") String description,
                                  @RequestParam(defaultValue = "") String location,
                                  RedirectAttributes redirect) {
        String problem = checkItemFields(itemName, description, location);
        if (problem != null) {
            redirect.addFlashAttribute("error", problem);
            return "redirect:/found/new";
        }
        repo.addFoundItem(itemName.trim(), description.trim(), location.trim());
        redirect.addFlashAttribute("message", "Found item reported successfully.");
        return "redirect:/found";
    }

    // ---------- Claims ----------

    @GetMapping("/claim")
    public String claimForm(@RequestParam Long itemId, Model model, RedirectAttributes redirect) {
        Optional<Item> item = repo.findFoundItem(itemId);
        if (item.isEmpty() || !"AVAILABLE".equals(item.get().getStatus())) {
            redirect.addFlashAttribute("error", "That item is not available for claiming.");
            return "redirect:/found";
        }
        model.addAttribute("item", item.get());
        return "claim-form";
    }

    @PostMapping("/claim")
    public String submitClaim(@RequestParam Long itemId,
                              @RequestParam String name,
                              @RequestParam String reason,
                              RedirectAttributes redirect) {
        Optional<Item> item = repo.findFoundItem(itemId);
        if (item.isEmpty() || !"AVAILABLE".equals(item.get().getStatus())) {
            redirect.addFlashAttribute("error", "That item is not available for claiming.");
            return "redirect:/found";
        }
        if (name.isBlank() || reason.isBlank() || name.length() > 100 || reason.length() > 1000) {
            redirect.addFlashAttribute("error", "Please enter your name (max 100 characters) and a reason (max 1000 characters).");
            return "redirect:/claim?itemId=" + itemId;
        }
        repo.addClaim(itemId, name.trim(), reason.trim());
        redirect.addFlashAttribute("message", "Claim submitted successfully. An administrator will review it.");
        return "redirect:/found";
    }

    // ---------- Administrator ----------

    @GetMapping("/admin")
    public String admin(Model model) {
        model.addAttribute("claims", repo.findAllClaims());
        return "admin";
    }

    @PostMapping("/admin/verify")
    public String verifyClaim(@RequestParam Long claimId,
                              @RequestParam String decision,
                              RedirectAttributes redirect) {
        if (!decision.equals("APPROVED") && !decision.equals("REJECTED")) {
            redirect.addFlashAttribute("error", "Invalid decision.");
            return "redirect:/admin";
        }
        if (repo.reviewClaim(claimId, decision)) {
            redirect.addFlashAttribute("message", "Claim " + decision.toLowerCase() + " successfully.");
        } else {
            redirect.addFlashAttribute("error", "That claim was not found or has already been reviewed.");
        }
        return "redirect:/admin";
    }

    // ---------- Helper ----------

    /** Returns an error message, or null when the fields are fine. */
    private String checkItemFields(String itemName, String description, String location) {
        if (itemName.isBlank()) {
            return "Item name is required.";
        }
        if (itemName.length() > 100) {
            return "Item name must be 100 characters or fewer.";
        }
        if (location.length() > 150) {
            return "Location must be 150 characters or fewer.";
        }
        if (description.length() > 1000) {
            return "Description must be 1000 characters or fewer.";
        }
        return null;
    }
}
