package dwayne.shim.gonggochatni.common.indexing;

import org.apache.lucene.document.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public enum JobDataIndexField {

    ID("id") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    URL("url") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    ACTIVE("active") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    POSTING_TIMESTAMP("posting-timestamp") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    POSTING_TIMESTAMP_SORT("posting-timestamp-sort") {
        @Override
        public Field buildField(String value) {
            return new SortedNumericDocValuesField(label(), Long.valueOf(value));
        }
    },
    MODIFICATION_TIMESTAMP("modification-timestamp") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    MODIFICATION_TIMESTAMP_SORT("modification-timestamp-sort") {
        @Override
        public Field buildField(String value) {
            return new SortedNumericDocValuesField(label(), Long.valueOf(value));
        }
    },
    OPENING_TIMESTAMP("opening-timestamp") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    EXPIRATION_TIMESTAMP("expiration-timestamp") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    EXPIRATION_TIMESTAMP_POINT("expiration-timestamp-point") {
        @Override
        public Field buildField(String value) {
            return new LongPoint(label(), Long.valueOf(value));
        }
    },
    CLOSE_TYPE("close-type") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    CLOSE_TYPE_CODE("close-type-code") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },

    COMPANY("company") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    COMPANY_NAME("company-name") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    COMPANY_NAME_SHORT("company-name-short") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    COMPANY_NAME_KEYWORDS("company-name-keyword") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    COMPANY_NAME_HREF("company-name-href") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },

    POSITION("position") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_TITLE("position-title") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_TITLE_SHORT("position-title-short") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_TITLE_KEYWORDS("company-title-keywords") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_LOCATION("position-location") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_LOCATION_CODE("position-location-code") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    POSITION_JOB_TYPE("position-job-type") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_JOB_TYPE_CODE("position-job-type-code") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    POSITION_INDUSTRY("position-industry") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_INDUSTRY_CODE("position-industry-code") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    POSITION_JOB_CATEGORY("position-job-category") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_JOB_CATEGORY_CODE("position-job-category-code") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },
    POSITION_OPEN_QUANTITY("position-open-quantity") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_EXPERIENCE_LEVEL("position-experience-level") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_EXPERIENCE_LEVEL_MIN("position-experience-level-min") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_EXPERIENCE_LEVEL_MAX("position-experience-level-max") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_REQUIRED_EDUCATION_LEVEL("position-required-education-level") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    POSITION_REQUIRED_EDUCATION_LEVEL_CODE("position-required-education-level-code") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    },

    KEYWORD("keyword") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    SALARY("salary") {
        @Override
        public Field buildField(String value) {
            return new TextField(label(), value, Field.Store.YES);
        }
    },
    SALARY_CODE("salary-code") {
        @Override
        public Field buildField(String value) {
            return new StringField(label(), value, Field.Store.YES);
        }
    };


    public abstract Field buildField(String value);

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
