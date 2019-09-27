package ru.gamingcore.staffstats.json;

import java.util.ArrayList;
import java.util.List;

public class Proj {
    public String proj_id = "";
    public String proj_name = "";
    public List<Allowance> allowances = new ArrayList<>();
    public List<Build> builds = new ArrayList<>();
    public List<Build> activeBuilds = new ArrayList<>();

    public boolean check;

    public boolean active = false; //
}