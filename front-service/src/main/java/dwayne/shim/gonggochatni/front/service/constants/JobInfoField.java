package dwayne.shim.gonggochatni.front.service.constants;

import dwayne.shim.gonggochatni.common.indexing.JobDataIndexField;
import org.apache.lucene.document.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum JobInfoField {

    ID("id"),
    URL("url"),
    ACTIVE("active"),
    POSTING_TIMESTAMP("posting-timestamp"),
    POSTING_TIMESTAMP_SORT("posting-timestamp-sort"),
    MODIFICATION_TIMESTAMP("modification-timestamp"),
    MODIFICATION_TIMESTAMP_SORT("modification-timestamp-sort"),
    OPENING_TIMESTAMP("opening-timestamp"),
    EXPIRATION_TIMESTAMP("expiration-timestamp"),
    EXPIRATION_TIMESTAMP_FORMATTED("expiration-timestamp-formatted"),
    EXPIRATION_TIMESTAMP_POINT("expiration-timestamp-point"),
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
    POSITION_LOCATION_SHORT("position-location-short"),
    POSITION_LOCATION_CODE("position-location-code"),
    POSITION_JOB_TYPE("position-job-type"),
    POSITION_JOB_TYPE_SHORT("position-job-type-short"),
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
    private JobInfoField(String _label) {
        label = _label;
    }

    public String label() {
        return label;
    }

}
