package fedilnik.android.data;

import java.util.ArrayList;
import java.util.List;

public class Meal {
    private String title;
    private List<String> content = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<String> getContent() {
        return content;
    }

    public void setContent(List<String> content) {
        this.content = content;
    }

    public void addContent(String content) {
        this.content.add(content);
    }
}
