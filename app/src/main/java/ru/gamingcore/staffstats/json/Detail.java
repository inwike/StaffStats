package ru.gamingcore.staffstats.json;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

public class Detail implements Comparator<Detail> {
    public String name = "";
    public String value = "";
    public String id = "";

    @Override
    public int compare(Detail a, Detail b) {
        return a.name.compareTo(b.name);
    }
}