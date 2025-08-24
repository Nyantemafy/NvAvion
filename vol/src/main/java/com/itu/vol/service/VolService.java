package com.itu.vol.service;

import com.itu.vol.model.Vol;
import com.itu.vol.repository.VolRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VolService {

    private final VolRepository volRepository;

    public VolService(VolRepository volRepository) {
        this.volRepository = volRepository;
    }

    public List<Vol> getAllVols() {
        return volRepository.findAll();
    }

    public Vol getVolById(Long idVol) {
        Optional<Vol> vol = volRepository.findById(idVol);
        return vol.orElse(null);
    }
}
