package com.yeonsung.modules.zone;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class

ZoneService {

    private final ZoneRepository zoneRepository;

    @PostConstruct
    public void initZoneData() throws IOException {
        if (zoneRepository.count() == 0) {
            Resource resource = new ClassPathResource("zones_kr.csv");

            InputStream zonesInputStream = resource.getInputStream();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(zonesInputStream)) ) {
                List<Zone> zoneList = reader.lines().map(line -> {
                    String[] split = line.split(",");
                    return Zone.builder().city(split[0]).localNameOfCity(split[1]).province(split[2]).build();
                }).collect(Collectors.toList());

                zoneRepository.saveAll(zoneList);
            }
        }
    }


}