package ai.elimu.model.content;

import javax.persistence.Entity;
import ai.elimu.model.BaseEntity;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.validation.constraints.NotNull;

@Entity
public class StoryBookParagraph extends BaseEntity {
    
    @ManyToOne
    private StoryBookChapter storyBookChapter;
    
    /**
     * [0, 1, 2, ...]
     */
    @NotNull
    private Integer sortOrder;
    
    @NotNull
    @Column(length = 1024)
    private String originalText;
    
    @OrderColumn
    @ManyToMany(fetch = FetchType.EAGER)
    private List<Word> words;

    public StoryBookChapter getStoryBookChapter() {
        return storyBookChapter;
    }

    public void setStoryBookChapter(StoryBookChapter storyBookChapter) {
        this.storyBookChapter = storyBookChapter;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public String getOriginalText() {
        return originalText;
    }

    public void setOriginalText(String originalText) {
        this.originalText = originalText;
    }

    public List<Word> getWords() {
        return words;
    }

    public void setWords(List<Word> words) {
        this.words = words;
    }

}
