package dwayne.shim.gonggochatni.front.service.constants;

public enum ModelField {

    JOB_INFO("job_info"),
    JOB_ADD_INFO("job_add_info"),
    JOB_DETAIL_INFO("job_detail_info"),
    JOB_SIMILAR_INFO("job_similar_info"),
    JOB_INTEREST_INFO("job_interest_info"),

    STATISTICS_SESSION_COUNT_LABELS("statistics_session_count_labels"),
    STATISTICS_SESSION_COUNT_VALUES("statistics_session_count_values");

    private String label;
    private ModelField(String _label) {
        label = _label;
    }

    public String label() {
        return label;
    }
}
