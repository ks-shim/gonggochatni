package dwayne.shim.gonggochatni.front.service.constants;

public enum AreaCode {

    CODE_101("101000", "서울"),
    CODE_102("102000", "경기"),
    CODE_103("103000", "광주"),
    CODE_104("104000", "대구"),
    CODE_105("105000", "대전"),
    CODE_106("106000", "부산"),
    CODE_107("107000", "울산"),
    CODE_108("108000", "인천"),
    CODE_109("109000", "강원"),
    CODE_110("110000", "경남"),
    CODE_111("111000", "경북"),
    CODE_112("112000", "전남"),
    CODE_113("113000", "전북"),
    CODE_114("114000", "충북"),
    CODE_115("115000", "충남"),
    CODE_116("116000", "제주"),
    CODE_117("117000", "전국"),
    CODE_118("118000", "세종"),
    CODE_210("210000", "아시아·중동"),
    CODE_220("220000", "북·중미"),
    CODE_230("230000", "남미"),
    CODE_240("240000", "유럽"),
    CODE_250("250000", "오세아니아"),
    CODE_260("260000", "아프리카"),
    CODE_270("270000", "남극대륙"),
    CODE_280("280000", "기타해외");

    private String code;
    private String label;

    private AreaCode(String _code,
                            String _label) {
        code = _code;
        label = _label;
    }

    public static String asLabel(String _code) {
        if(_code.length() != 6) return "기타";

        _code = _code.substring(0, 3) + "000";
        for(AreaCode areaCode : values())
            if(areaCode.code.equals(_code)) return areaCode.label;

        return "기타";
    }
}
