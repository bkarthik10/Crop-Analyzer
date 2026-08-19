package com.smartfarming.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Output of POST /api/fertilizer/recommend.
 *
 * <p>Deliberately has no dosage/application-timing fields: the fertilizer
 * dataset only labels a product name, not per-acre quantities or schedules,
 * and inventing precise numbers without reliable reference data would be
 * misleading. {@code usageNote} carries general, well-established agronomic
 * facts about the recommended product instead (e.g. approximate N-P-K
 * composition) — exact dosage should come from a soil test or agronomist.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FertilizerResponse {

    private String fertilizerName;
    private String reasoning;
    private String usageNote;
    private String method;   // how this recommendation was produced, for UI transparency
}
