package com.cnhalo.largierj.ctrl.admin;

import com.cnhalo.largierj.dt.Event;
import com.cnhalo.largierj.dt.ImageSavedInfo;
import com.cnhalo.largierj.dt.Result;
import com.cnhalo.largierj.dt.UpdateVisibleCommand;
import com.cnhalo.largierj.model.Concert;
import com.cnhalo.largierj.service.AdminService;
import com.cnhalo.largierj.service.FileUploadService;
import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * Created by Henry Huang on 2022/9/25.
 */
@RestController
@RequestMapping("/api/admin/events")
public class EventController {

    final
    AdminService adminService;

    final
    FileUploadService fileUploadService;

    public EventController(AdminService adminService, FileUploadService fileUploadService) {
        this.adminService = adminService;
        this.fileUploadService = fileUploadService;
    }

    @GetMapping("")
    public ResponseEntity<List<Event>> findAllEvents() {
        return ResponseEntity.ok(adminService.findAllEvents());
    }

    @PostMapping(value = "/add")
    public ResponseEntity<Result<Event>> addEventNew(@RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam("event") Event event) {
        ImageSavedInfo imageSavedInfo = file == null ?  null : fileUploadService.checkAndSaveImage(file);
        Event added = adminService.createConcert(event, imageSavedInfo);
        return ResponseEntity.ok(Result.<Event>builder().ok(true).data(added).build());
    }

    @GetMapping("/item/{id}")
    public ResponseEntity<Result<Event>> findEvent(@PathVariable @NotNull Long id, @RequestParam(value = "update", required = false) Boolean update) {
        return ResponseEntity.ok(Result.<Event>builder().ok(true).data(adminService.findEvent(id, update)).build());
    }

    @PostMapping(value = "/item/{id}")
    public ResponseEntity<Result<Event>> updateEvent(@PathVariable @NotNull Long id, @RequestParam(value = "file", required = false) MultipartFile file,
        @RequestParam("event") Event event) {
        ImageSavedInfo imageSavedInfo = file == null ?  null : fileUploadService.checkAndSaveImage(file);
        Event updated = adminService.updateConcert(id, event, imageSavedInfo);
        return ResponseEntity.ok(Result.<Event>builder().ok(true).data(updated).build());
    }

    @PostMapping("/visible")
    public ResponseEntity<Result<List<Event>>> updateEventVisible(@RequestBody @Valid UpdateVisibleCommand command) {
        adminService.updateConcertsVisible(command.getIds(), command.isVisible());
        return ResponseEntity.ok(Result.<List<Event>>builder().ok(true).build());
    }

    @PostMapping("/delete")
    public ResponseEntity<Result<Event>> deleteEvent(@RequestBody List<Long> ids) {
        adminService.getConcertRepository().deleteAllById(ids);
        return ResponseEntity.ok(Result.<Event>builder().ok(true).build());
    }

}
