package dwayne.shim.gonggochatni.common.searching;

public enum LuceneResultField {

    SCORE("score");

    private String label;
    private LuceneResultField(String _label) {
        label = _label;
    }

    public String label() {
        return label;
    }
}
