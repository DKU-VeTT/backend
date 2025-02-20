package kr.ac.dankook.VettAuthServer.entity;

public enum OutboxStatus {
    READY_TO_PUBLISH,
    PUBLISHED,
    MESSAGE_CONSUME,
    FAILED,
    PERMANENTLY_FAILED
}