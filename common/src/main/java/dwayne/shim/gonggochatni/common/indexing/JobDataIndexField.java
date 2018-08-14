package dwayne.shim.gonggochatni.common.indexing;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum JobDataIndexField {

    ID("id"),
    URL("url"),
    ACTIVE("active"),
    POSTING_TIMESTAMP("posting-timestamp"),
    MODIFICATION_TIMESTAMP("modification-timestamp"),
    OPENING_TIMESTAMP("opening-timestamp"),
    EXPIRATION_TIMESTAMP("expiration-timestamp"),

    CLOSE_TYPE("close-type"),
    CLOSE_TYPE_CODE("close-type-code"),

    COMPANY("company"),
    COMPANY_NAME("company-name"),
    COMPANY_NAME_SHORT("company-name-short"),
    COMPANY_NAME_KEYWORDS("company-name-keyword"),
    COMPANY_NAME_HREF("company-name-href"),

    POSITION("position"),
    POSITION_TITLE("position-title"),
    POSITION_TITLE_SHORT("position-title-short"),
    POSITION_TITLE_KEYWORDS("company-title-keywords"),
    POSITION_LOCATION("position-location"),
    POSITION_LOCATION_CODE("position-location-code"),
    POSITION_JOB_TYPE("position-job-type"),
    POSITION_JOB_TYPE_CODE("position-job-type-code"),
    POSITION_INDUSTRY("position-industry"),
    POSITION_INDUSTRY_CODE("position-industry-code"),
    POSITION_JOB_CATEGORY("position-job-category"),
    POSITION_JOB_CATEGORY_CODE("position-job-category-code"),
    POSITION_OPEN_QUANTITY("position-open-quantity"),
    POSITION_EXPERIENCE_LEVEL("position-experience-level"),
    POSITION_EXPERIENCE_LEVEL_MIN("position-experience-level-min"),
    POSITION_EXPERIENCE_LEVEL_MAX("position-experience-level-max"),
    POSITION_REQUIRED_EDUCATION_LEVEL("position-required-education-level"),
    POSITION_REQUIRED_EDUCATION_LEVEL_CODE("position-required-education-level-code"),

    KEYWORD("keyword"),
    SALARY("salary"),
    SALARY_CODE("salary-code");


    private final String label;
    private JobDataIndexField(String _label) {
        label = _label;
    }

    public String label() {
        return label;
    }

    public static Map<String, JobDataIndexField> map() {
        Map<String, JobDataIndexField> map = new HashMap<>();
        for(JobDataIndexField field : values())
            map.put(field.label, field);
        return Collections.unmodifiableMap(map);
    }
}
