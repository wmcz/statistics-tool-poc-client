package cz.cvut.fit.wikimetric.pocclient.model;

public class EventImpact {
    public int createdPages;
    public int editedPages;
    public int edits;
    public int uploadedFiles;
    public int pageViews;

    public EventImpact(int createdPages, int editedPages, int edits, int uploadedFiles, int pageViews) {
        this.createdPages = createdPages;
        this.editedPages = editedPages;
        this.edits = edits;
        this.uploadedFiles = uploadedFiles;
        this.pageViews = pageViews;
    }
}
