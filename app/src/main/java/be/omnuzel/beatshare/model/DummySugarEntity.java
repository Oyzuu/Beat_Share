package be.omnuzel.beatshare.model;

import com.orm.SugarRecord;

/**
 * Created by isdc on 9/11/16.
 */

public class DummySugarEntity extends SugarRecord {

    String name;
    Integer count;

    public DummySugarEntity() {

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getCount() {
        return count;
    }

    public void setCount(Integer count) {
        this.count = count;
    }
}
