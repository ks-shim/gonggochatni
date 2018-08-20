package dwayne.shim.gonggochatni.front.service.constants;

public enum JobCategoryCode {

    CODE_1("1", "경영·사무"),
    CODE_2("2", "영업·고객상담"),
    CODE_3("3", "생산·제조"),
    CODE_4("4", "IT·인터넷"),
    CODE_5("5", "전문직"),
    CODE_6("6", "교육"),
    CODE_7("7", "미디어"),
    CODE_8("8", "특수계층·공공"),
    CODE_9("9", "건설"),
    CODE_10("10", "유통·무역"),
    CODE_11("11", "서비스"),
    CODE_12("12", "디자인"),
    CODE_13("13", "의료");

    private String code;
    private String label;

    private JobCategoryCode(String _code,
                            String _label) {
        code = _code;
        label = _label;
    }

    public static String asLabel(String _code) {
        if(_code.length() < 3 || _code.length() > 4) return "기타";
        else if(_code.length() == 3) _code = _code.substring(0, 1);
        else _code = _code.substring(0, 2);

        for(JobCategoryCode categoryCode : values())
            if(categoryCode.code.equals(_code)) return categoryCode.label;

        return "기타";
    }
}
