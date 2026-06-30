package com.egitim.portal.task;

/** Görev bulunamadığında fırlatılır -> GlobalExceptionHandler 404'e çevirir. */
public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(Long id) {
        super("Görev bulunamadı: id=" + id);
    }
}
