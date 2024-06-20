package tech.buildrun.poi.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tech.buildrun.poi.controller.dto.CreatePointOfInterest;
import tech.buildrun.poi.entity.PointOfInterest;
import tech.buildrun.poi.repository.PointOfInterestRepository;

import java.util.List;

@RestController
public class PointOfInterestController {

    private final PointOfInterestRepository pointOfInterestRepository;

    public PointOfInterestController(PointOfInterestRepository pointOfInterestRepository) {
        this.pointOfInterestRepository = pointOfInterestRepository;
    }

    @PostMapping("/points-of-interests")
    public ResponseEntity<Void> createPoi(@RequestBody CreatePointOfInterest body) {

        pointOfInterestRepository.save(new PointOfInterest(body.name(), body.y(), body.y()));

        return ResponseEntity.ok().build()
;    }

    @GetMapping("/points-of-interests")
    public ResponseEntity<Page<PointOfInterest>> listPoi(@RequestParam(name = "page", defaultValue = "0") Integer page,
                                                         @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize) {

        var body = pointOfInterestRepository.findAll(PageRequest.of(page, pageSize));

        return ResponseEntity.ok(body);
    }

    @GetMapping("/near-me")
    public ResponseEntity<List<PointOfInterest>> listPoi(@RequestParam("x") Long x,
                                                         @RequestParam("y") Long y,
                                                         @RequestParam("dmax") Long dmax) {
        var xMin = x - dmax;
        var xMax = x + dmax;
        var yMin = y - dmax;
        var yMax = y + dmax;

        var body = pointOfInterestRepository.findNearMe(xMin, xMax, yMin, yMax)
                .stream()
                .filter(p -> distanceBetweenPoints(x, y, p.getX(), p.getY()) <= dmax)
                .toList();

        return ResponseEntity.ok(body);
    }

    private Double distanceBetweenPoints(Long x1, Long y1, Long x2, Long y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }}
