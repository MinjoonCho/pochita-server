package com.pochita.server.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "sessions")
public class DistractionSession {

    @Id
    private String id;

    @Column(nullable = false)
    private String userId;

    @Column(nullable = false)
    private String categoryId;

    @Column(nullable = false)
    private long startTime;

    private Long endTime;

    private Long duration;

    @Column(length = 1000)
    private String note;

    protected DistractionSession() {
    }

    public DistractionSession(String id, String userId, String categoryId, long startTime) {
        this.id = id;
        this.userId = userId;
        this.categoryId = categoryId;
        this.startTime = startTime;
    }

    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public long getStartTime() {
        return startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public Long getDuration() {
        return duration;
    }

    public String getNote() {
        return note;
    }

    public boolean isCompleted() {
        return endTime != null && duration != null;
    }

    public void finish(long endTime, long duration, String note) {
        this.endTime = endTime;
        this.duration = duration;
        this.note = note;
    }
}
