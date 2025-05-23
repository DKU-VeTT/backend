package kr.ac.dankook.VettDiagnosisServer.service;

import kr.ac.dankook.VettDiagnosisServer.repository.PhotoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PhotoService {

    private final PhotoRepository photoRepository;
}
