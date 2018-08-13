package dwayne.shim.gonggochatni.crawler;

import lombok.extern.log4j.Log4j2;

import java.util.Map;

@Log4j2
public class JobCrawler {

    enum ParameterKey {
        START("start"),
        COUNT("count");

        private final String label;
        private ParameterKey(String _label) {
            label = _label;
        }

        public String labe() {
            return label;
        }
    }

    enum XMLNode {

        JOBS("jobs"),
        JOB("job"),

        ID("id"),
        URL("url"),
        ACTIVE("active"),
        POSTING_TIMESTAMP("posting-timestamp"),
        MODIFICATION_TIMESTAMP("modification-timestamp"),
        OPENING_TIMESTAMP("opening-timestamp"),
        EXPIRATION_TIMESTAMP("expiration-timestamp"),

        CLOSE_TYPE("close-type"),
        CLOSE_TYPE_CODE("code"),

        COMPANY("company"),
        COMPANY_NAME("name"),
        COMPNAY_NAME_HREF("href"),

        POSITION("position"),
        POSITION_TITLE("title"),
        POSITION_LOCATION("location"),
        POSITION_LOCATION_CODE("code"),
        POSITION_JOB_TYPE("job-type"),
        POSITION_JOB_TYPE_CODE("code"),
        POSITION_INDUSTRY("industry"),
        POSITION_INDUSTRY_CODE("code"),
        POSITION_JOB_CATEGORY("job-category"),
        POSITION_JOB_CATEGORY_CODE("code"),
        POSITION_OPEN_QUANTITY("open-quantity"),
        POSITION_EXPERIENCE_LEVEL("experience-level"),
        POSITION_EXPERIENCE_LEVEL_MIN("min"),
        POSITION_EXPERIENCE_LEVEL_MAX("max"),
        POSITION_REQUIRED_EDUCATION_LEVEL("required-education-level"),
        POSITION_REQUIRED_EDUCATION_LEVEL_CODE("code"),

        KEYWORD("keyword"),
        SALARY("salary"),
        SALARY_CODE("code");


        private final String label;
        private XMLNode(String _label) {
            label = _label;
        }

        public String labe() {
            return label;
        }
    }

    public static void main(String[] args) throws Exception {


    }

    private static class RestApiInfo {

        private final String baseUrl;
        private final Map<ParameterKey, String> parameters;

        public RestApiInfo(String baseUrl,
                           Map<ParameterKey, String> parameters) {
            this.baseUrl = baseUrl;
            this.parameters = parameters;
        }

        public String asUrlStringWith(Map<ParameterKey, String> newParams) {

            // 1. put pre-existing parameters into newParams ...
            for(ParameterKey key : parameters.keySet())
                newParams.putIfAbsent(key, parameters.get(key));

            // 2. make url (baseUrl + parameters)
            StringBuilder sb = new StringBuilder();
            sb.append(baseUrl).append('?');

            boolean isFirst = true;
            for(ParameterKey key : newParams.keySet()) {
                if(!isFirst) sb.append('&');
                else isFirst = false;

                sb.append(key.label).append('=').append(newParams.get(key));
            }

            return sb.toString();
        }
    }
}
