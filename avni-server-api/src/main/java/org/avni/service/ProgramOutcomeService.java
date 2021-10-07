package org.avni.service;

import org.joda.time.DateTime;
import org.avni.dao.ProgramOutcomeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProgramOutcomeService implements NonScopeAwareService {

    private final ProgramOutcomeRepository programOutcomeRepository;

    @Autowired
    public ProgramOutcomeService(ProgramOutcomeRepository programOutcomeRepository) {
        this.programOutcomeRepository = programOutcomeRepository;
    }

    @Override
    public boolean isNonScopeEntityChanged(DateTime lastModifiedDateTime) {
        return programOutcomeRepository.existsByAuditLastModifiedDateTimeGreaterThan(lastModifiedDateTime);
    }
}