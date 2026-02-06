package org.cswteams.ms3.ai.decision;

import java.util.List;

public interface DecisionAlgorithmService {

    AiScheduleCandidateMetrics selectPreferred(List<AiScheduleCandidateMetrics> candidates);
}
