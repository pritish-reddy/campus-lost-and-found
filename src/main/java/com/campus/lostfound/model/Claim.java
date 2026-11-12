package com.campus.lostfound.model;

/** One row of the claims table. itemName comes from the related found item. */
public class Claim {

    private Long id;
    private Long foundItemId;
    private String claimantName;
    private String claimReason;
    private String status;
    private String itemName;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getFoundItemId() { return foundItemId; }
    public void setFoundItemId(Long foundItemId) { this.foundItemId = foundItemId; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimReason() { return claimReason; }
    public void setClaimReason(String claimReason) { this.claimReason = claimReason; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }
}
