package com.campus.lostfound.repository;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.Item;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/** All database work lives here. Every query uses ? placeholders (parameterized queries). */
@Repository
public class ItemRepository {

    private final JdbcTemplate jdbc;

    public ItemRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    // ---------- Lost items ----------

    public void addLostItem(String itemName, String description, String location) {
        jdbc.update("INSERT INTO lost_items (item_name, description, location) VALUES (?, ?, ?)",
                itemName, description, location);
    }

    public List<Item> findLostItems(String search) {
        String like = "%" + search + "%";
        return jdbc.query(
                "SELECT * FROM lost_items " +
                "WHERE item_name ILIKE ? OR COALESCE(description, '') ILIKE ? OR COALESCE(location, '') ILIKE ? " +
                "ORDER BY id DESC",
                new BeanPropertyRowMapper<>(Item.class), like, like, like);
    }

    // ---------- Found items ----------

    public void addFoundItem(String itemName, String description, String location) {
        jdbc.update("INSERT INTO found_items (item_name, description, location) VALUES (?, ?, ?)",
                itemName, description, location);
    }

    public List<Item> findFoundItems(String search) {
        String like = "%" + search + "%";
        return jdbc.query(
                "SELECT * FROM found_items " +
                "WHERE item_name ILIKE ? OR COALESCE(description, '') ILIKE ? OR COALESCE(location, '') ILIKE ? " +
                "ORDER BY id DESC",
                new BeanPropertyRowMapper<>(Item.class), like, like, like);
    }

    public Optional<Item> findFoundItem(Long id) {
        return jdbc.query("SELECT * FROM found_items WHERE id = ?",
                new BeanPropertyRowMapper<>(Item.class), id).stream().findFirst();
    }

    // ---------- Claims ----------

    public void addClaim(Long foundItemId, String claimantName, String claimReason) {
        // status is not given, so the database default 'PENDING' is used
        jdbc.update("INSERT INTO claims (found_item_id, claimant_name, claim_reason) VALUES (?, ?, ?)",
                foundItemId, claimantName, claimReason);
    }

    /** All claims, pending ones first, together with the name of the claimed item. */
    public List<Claim> findAllClaims() {
        return jdbc.query(
                "SELECT c.id, c.found_item_id, c.claimant_name, c.claim_reason, c.status, f.item_name " +
                "FROM claims c JOIN found_items f ON f.id = c.found_item_id " +
                "ORDER BY (c.status = 'PENDING') DESC, c.id DESC",
                new BeanPropertyRowMapper<>(Claim.class));
    }

    /**
     * Approves or rejects a PENDING claim.
     * When a claim is approved, the found item becomes CLAIMED and any other
     * pending claims for the same item are rejected.
     *
     * @return false if the claim does not exist or was already reviewed
     */
    @Transactional
    public boolean reviewClaim(Long claimId, String decision) {
        int updated = jdbc.update("UPDATE claims SET status = ? WHERE id = ? AND status = 'PENDING'",
                decision, claimId);
        if (updated == 0) {
            return false;
        }
        if (decision.equals("APPROVED")) {
            jdbc.update("UPDATE found_items SET status = 'CLAIMED' " +
                        "WHERE id = (SELECT found_item_id FROM claims WHERE id = ?)", claimId);
            jdbc.update("UPDATE claims SET status = 'REJECTED' " +
                        "WHERE status = 'PENDING' " +
                        "AND found_item_id = (SELECT found_item_id FROM claims WHERE id = ?)", claimId);
        }
        return true;
    }

    // ---------- Numbers for the home page ----------

    public int countLostItems() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM lost_items", Integer.class);
    }

    public int countAvailableFoundItems() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM found_items WHERE status = 'AVAILABLE'", Integer.class);
    }

    public int countPendingClaims() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM claims WHERE status = 'PENDING'", Integer.class);
    }
}
