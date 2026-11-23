package edu.unc.comp301.dungeon.view;

public enum Theme {
    DARK("theme-dark", "dark", "DARK"),
    LIGHT("theme-light", "light", "LIGHT");

    private final String cssClass;
    private final String folder;
    private final String label;

    Theme(String cssClass, String folder, String label) {
        this.cssClass = cssClass;
        this.folder = folder;
        this.label = label;
    }

    public String getCssClass() { return cssClass; }
    public String getFolder() { return folder; }
    public String getLabel() { return label; }

    public Theme next() {
        return this == DARK ? LIGHT : DARK;
    }
}