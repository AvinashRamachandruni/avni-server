package org.avni.server.web.request.api;

import org.avni.server.dao.ConceptRepository;
import org.avni.server.domain.Concept;
import org.avni.server.domain.ConceptDataType;
import org.avni.server.domain.ObservationCollection;
import org.avni.server.util.BadRequestError;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class RequestUtils {
    public static ObservationCollection createObservations(Map<String, Object> observationsRequest, ConceptRepository conceptRepository) {
        Map<String, Object> observations = new HashMap<>();
        for (Map.Entry<String, Object> entry : observationsRequest.entrySet()) {
            putObservation(conceptRepository, observations, entry);
        }
        return new ObservationCollection(observations);
    }

    public static void patchObservations(Map<String, Object> observationsRequest, ConceptRepository conceptRepository, ObservationCollection observations) {
        for (Map.Entry<String, Object> entry : observationsRequest.entrySet()) {
            putObservation(conceptRepository, observations, entry);
        }
    }

    private static void putObservation(ConceptRepository conceptRepository, Map<String, Object> observations, Map.Entry<String, Object> entry) {
        String conceptName = entry.getKey();
        Concept concept = conceptRepository.findByName(conceptName);
        if (concept == null) {
            throw new NullPointerException(String.format("Concept with name=%s not found", conceptName));
        }
        String conceptUUID = concept.getUuid();
        String conceptDataType = concept.getDataType();
        Object obsValue;
        Object entryValue = entry.getValue();

        if (entryValue == null) return;

        obsValue = getObsValue(conceptRepository, conceptDataType, entryValue);
        observations.put(conceptUUID, obsValue);
    }

    private static Object getObsValue(ConceptRepository conceptRepository, String conceptDataType, Object entryValue) {
        Object obsValue;
        switch (ConceptDataType.valueOf(conceptDataType)) {
            case Coded: {
                if (entryValue instanceof Collection<?>) {
                    obsValue = ((List<String>) entryValue).stream().map(answerConceptName -> {
                        Concept answerConcept = conceptRepository.findByName(answerConceptName);
                        if (answerConcept == null)
                            throw new BadRequestError(String.format("Answer concept with name=%s not found", answerConceptName));
                        return answerConcept.getUuid();
                    }).collect(Collectors.toList());
                } else {
                    String answerConceptName = (String) entryValue;
                    Concept answerConcept = conceptRepository.findByName(answerConceptName);
                    if (answerConcept == null)
                        throw new BadRequestError(String.format("Answer concept with name=%s not found", answerConceptName));
                    obsValue = answerConcept.getUuid();
                }
                break;
            }
            case QuestionGroup: {
                if (entryValue instanceof Collection<?>) {
                    List<ObservationCollection> groupOfChildObservations = new ArrayList<>();
                    for (Object o : ((Collection<?>) entryValue)) {
                        Map<String, Object> childObsCollection = (Map<String, Object>) o;
                        ObservationCollection childObservations = createObservations(childObsCollection, conceptRepository);
                        groupOfChildObservations.add(childObservations);
                    }
                    obsValue = groupOfChildObservations;
                } else {
                    Map<String, Object> childObsCollection = (Map<String, Object>) entryValue;
                    obsValue = createObservations(childObsCollection, conceptRepository);
                }
                break;
            }
            default: {
                obsValue = entryValue;
                break;
            }
        }
        return obsValue;
    }

    public static boolean isValidUUID(String text){
        try {
            return StringUtils.hasText(text) && UUID.fromString(text.trim()) != null;
        } catch (Exception e) {
            return false;
        }
    }
}
