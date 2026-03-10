package com.logistics.controller;

import com.logistics.common.Result;
import com.logistics.entity.Station;
import com.logistics.service.StationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/stations")
@RequiredArgsConstructor
public class StationController {

    private final StationService stationService;

    @GetMapping
    public Result<?> list() {
        return Result.success(stationService.list());
    }

    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        Station station = stationService.getById(id);
        if (station == null) {
            return Result.error("站点不存在");
        }
        return Result.success(station);
    }

    @PostMapping
    public Result<?> create(@RequestBody Station station) {
        station.setStationCode("ST" + System.currentTimeMillis());
        station.setCreateTime(LocalDateTime.now());
        stationService.save(station);
        return Result.success();
    }

    @PutMapping("/{id}")
    public Result<?> update(@PathVariable Long id, @RequestBody Station station) {
        station.setId(id);
        stationService.updateById(station);
        return Result.success();
    }

    @DeleteMapping("/{id}")
    public Result<?> delete(@PathVariable Long id) {
        stationService.removeById(id);
        return Result.success();
    }
}
