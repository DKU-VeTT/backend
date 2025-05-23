package kr.ac.dankook.VettDiagnosisServer.service;

import kr.ac.dankook.VettDiagnosisServer.repository.DiagnosisRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DiagnosisService {

    private final DiagnosisRepository diagnosisRepository;
}
