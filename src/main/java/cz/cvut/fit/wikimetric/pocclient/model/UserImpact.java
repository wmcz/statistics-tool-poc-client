package cz.cvut.fit.wikimetric.pocclient.model;

public class UserImpact {
    public int createdPages;
    public int editedPages;
    public int edits;
    public int uploadedFiles;
    public int pageViews;

    public UserImpact(int createdPages, int editedPages, int edits, int uploadedFiles, int pageViews) {
        this.createdPages = createdPages;
        this.editedPages = editedPages;
        this.edits = edits;
        this.uploadedFiles = uploadedFiles;
        this.pageViews = pageViews;
    }

    @Override
    public String toString() {
        return "UserImpact{" +
                "createdPages=" + createdPages +
                ", editedPages=" + editedPages +
                ", edits=" + edits +
                ", uploadedFiles=" + uploadedFiles +
                ", pageViews=" + pageViews +
                '}';
    }
}
