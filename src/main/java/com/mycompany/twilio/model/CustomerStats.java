package com.mycompany.twilio.model;

/**
 *
 * @author Ali
 */
/**
 * Holds aggregated SMS statistics for a single customer. Used by AdminDao and
 * CustomerStatsServlet.
 */
public class CustomerStats {

    private int userId;
    private String fullName;
    private String email;
    private String msisdn;
    private int totalSent;
    private int totalReceived;
    private String lastActivityAt;

    // Pre-calculated 0-100 percentages set by AdminDao after finding the max
    private int sentPct = 0;
    private int receivedPct = 0;

    // ── Constructors ─────────────────────────────────────────────────────────
    public CustomerStats() {
    }

    public CustomerStats(int userId, String fullName, String email,
            String msisdn, int totalSent, int totalReceived,
            String lastActivityAt) {
        this.userId = userId;
        this.fullName = fullName;
        this.email = email;
        this.msisdn = msisdn;
        this.totalSent = totalSent;
        this.totalReceived = totalReceived;
        this.lastActivityAt = lastActivityAt;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────
    public int getUserId() {
        return userId;
    }

    public void setUserId(int v) {
        this.userId = v;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String v) {
        this.fullName = v;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String v) {
        this.email = v;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String v) {
        this.msisdn = v;
    }

    public int getTotalSent() {
        return totalSent;
    }

    public void setTotalSent(int v) {
        this.totalSent = v;
    }

    public int getTotalReceived() {
        return totalReceived;
    }

    public void setTotalReceived(int v) {
        this.totalReceived = v;
    }

    public String getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(String v) {
        this.lastActivityAt = v;
    }

    public int getSentPct() {
        return sentPct;
    }

    public void setSentPct(int v) {
        this.sentPct = v;
    }

    public int getReceivedPct() {
        return receivedPct;
    }

    public void setReceivedPct(int v) {
        this.receivedPct = v;
    }

    /**
     * Total messages touched (sent + received).
     */
    public int getTotalMessages() {
        return totalSent + totalReceived;
    }

    /**
     * Calculates sentPct and receivedPct relative to a given maximum. Call this
     * after loading all stats rows and finding the max totalSent.
     */
    public void calculatePcts(int maxSent) {
        if (maxSent <= 0) {
            this.sentPct = 0;
            this.receivedPct = 0;
        } else {
            this.sentPct = (totalSent * 100) / maxSent;
            this.receivedPct = (totalReceived * 100) / maxSent;
        }
    }
}
